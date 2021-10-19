/*
 * Copyright (C) 2018 jirui_zhao(jirui_zhao@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rui.mvvmlazy.utils.common

import com.rui.mvvmlazy.utils.data.DateUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 * desc   :	身份证工具类
 * author : zjr
 * time   : 2018/4/23 下午4:21
</pre> *
 */
class IDCardUtils() {
    companion object {
        /**
         * 中国公民身份证号码最小长度。
         */
        const val CHINA_ID_MIN_LENGTH = 15

        /**
         * 中国公民身份证号码最大长度。
         */
        const val CHINA_ID_MAX_LENGTH = 18

        /**
         * 省、直辖市代码表
         */
        val cityCode = arrayOf(
            "11",
            "12",
            "13",
            "14",
            "15",
            "21",
            "22",
            "23",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "50",
            "51",
            "52",
            "53",
            "54",
            "61",
            "62",
            "63",
            "64",
            "65",
            "71",
            "81",
            "82",
            "91"
        )

        /**
         * 每位加权因子
         */
        val power = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)

        /**
         * 第18位校检码
         */
        val verifyCode = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")

        /**
         * 最低年限
         */
        const val MIN = 1930
        val cityCodes: MutableMap<String, String?> = HashMap()

        /**
         * 台湾身份首字母对应数字
         */
        val twFirstCode: MutableMap<String, Int> = HashMap()

        /**
         * 香港身份首字母对应数字
         */
        val hkFirstCode: MutableMap<String, Int> = HashMap()

        /**
         * 将15位身份证号码转换为18位
         *
         * @param idCard 15位身份编码
         * @return 18位身份编码
         */
        private fun convert15CardTo18(idCard: String?): String? {
            var idCard18 = ""
            if (idCard!!.length != CHINA_ID_MIN_LENGTH) {
                return null
            }
            if (isNum(idCard)) {
                // 获取出生年月日
                val birthday = idCard.substring(6, 12)
                var birthDate: Date? = null
                try {
                    birthDate = SimpleDateFormat("yyMMdd").parse(birthday)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val cal = Calendar.getInstance()
                if (birthDate != null) {
                    cal.time = birthDate
                }
                // 获取出生年(完全表现形式,如：2010)
                val sYear = cal[Calendar.YEAR].toString()
                idCard18 = idCard.substring(0, 6) + sYear + idCard.substring(8)
                // 转换字符数组
                val cArr = idCard18.toCharArray()
                val iCard = convertCharToInt(cArr)
                val iSum17 = getPowerSum(iCard)
                // 获取校验位
                val sVal = getCheckCode18(iSum17)
                idCard18 += if (sVal.length > 0) {
                    sVal
                } else {
                    return null
                }
            } else {
                return null
            }
            return idCard18
        }

        /**
         * 验证18位身份编码是否合法
         *
         * @param idCard 身份编码
         * @return 是否合法
         */
        private fun validateIdCard18(idCard: String): Boolean {
            var bTrue = false
            if (idCard.length == CHINA_ID_MAX_LENGTH) {
                // 前17位
                val code17 = idCard.substring(0, 17)
                // 第18位
                val code18 = idCard.substring(17, CHINA_ID_MAX_LENGTH)
                if (isNum(code17)) {
                    val cArr = code17.toCharArray()
                    val iCard = convertCharToInt(cArr)
                    val iSum17 = getPowerSum(iCard)
                    // 获取校验位
                    val `val` = getCheckCode18(iSum17)
                    if (`val`.length > 0) {
                        if (`val`.equals(code18, ignoreCase = true)) {
                            bTrue = true
                        }
                    }
                }
            }
            return bTrue
        }

        /**
         * 验证15位身份编码是否合法
         *
         * @param idCard 身份编码
         * @return 是否合法
         */
        private fun validateIdCard15(idCard: String): Boolean {
            if (idCard.length != CHINA_ID_MIN_LENGTH) {
                return false
            }
            return if (isNum(idCard)) {
                val proCode = idCard.substring(0, 2)
                if (cityCodes[proCode] == null) {
                    return false
                }
                val birthCode = idCard.substring(6, 12)
                var birthDate: Date? = null
                try {
                    birthDate = SimpleDateFormat("yy").parse(birthCode.substring(0, 2))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val cal = Calendar.getInstance()
                if (birthDate != null) {
                    cal.time = birthDate
                }
                validate(
                    cal[Calendar.YEAR],
                    birthCode.substring(2, 4).toInt(),
                    birthCode.substring(4, 6).toInt()
                )
            } else {
                false
            }
        }

        /**
         * 验证10位身份编码是否合法
         *
         * @param idCard 身份编码
         * @return 身份证信息数组
         *
         *
         * [0] - 台湾、澳门、香港 [1] - 性别(男M,女F,未知N) [2] - 是否合法(合法true,不合法false)
         * 若不是身份证件号码则返回null
         *
         */
        private fun validateIdCard10(idCard: String): Array<String?>? {
            val info = arrayOfNulls<String>(3)
            val card = idCard.replace("[\\(|\\)]".toRegex(), "")
            if (card.length != 8 && card.length != 9 && idCard.length != 10) {
                return null
            }
            if (idCard.matches(Regex("^[a-zA-Z][0-9]{9}$"))) { // 台湾
                info[0] = "台湾"
                println("11111")
                val char2 = idCard.substring(1, 2)
                if ("1" == char2) {
                    info[1] = "M"
                    println("MMMMMMM")
                } else if ("2" == char2) {
                    info[1] = "F"
                    println("FFFFFFF")
                } else {
                    info[1] = "N"
                    info[2] = "false"
                    println("NNNN")
                    return info
                }
                info[2] = if (validateTWCard(idCard)) "true" else "false"
            } else if (idCard.matches(Regex("^[1|5|7][0-9]{6}\\(?[0-9A-Z]\\)?$"))) { // 澳门
                info[0] = "澳门"
                info[1] = "N"
            } else if (idCard.matches(Regex("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$"))) { // 香港
                info[0] = "香港"
                info[1] = "N"
                info[2] = if (validateHKCard(idCard)) "true" else "false"
            } else {
                return null
            }
            return info
        }

        /**
         * 验证台湾身份证号码
         *
         * @param idCard 身份证号码
         * @return 验证码是否符合
         */
        fun validateTWCard(idCard: String): Boolean {
            val start = idCard.substring(0, 1)
            val mid = idCard.substring(1, 9)
            val end = idCard.substring(9, 10)
            val iStart = twFirstCode[start]
            var sum = iStart!! / 10 + iStart % 10 * 9
            val chars = mid.toCharArray()
            var iflag = 8
            for (c in chars) {
                sum = sum + (c.toString() + "").toInt() * iflag
                iflag--
            }
            return (if (sum % 10 == 0) 0 else 10 - sum % 10) == end.toInt()
        }

        /**
         * 验证香港身份证号码(存在Bug，部份特殊身份证无法检查)
         *
         *
         * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35
         * 最后一位校验码为0-9的数字加上字符"A"，"A"代表10
         *
         *
         *
         * 将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效
         *
         *
         * @param idCard 身份证号码
         * @return 验证码是否符合
         */
        fun validateHKCard(idCard: String): Boolean {
            var card = idCard.replace("[\\(|\\)]".toRegex(), "")
            var sum = 0
            if (card.length == 9) {
                sum = (card.substring(0, 1).toUpperCase().toCharArray()[0]
                    .toInt() - 55) * 9 + (card.substring(1, 2).toUpperCase()
                    .toCharArray()[0].toInt() - 55) * 8
                card = card.substring(1, 9)
            } else {
                sum = 522 + (card.substring(0, 1).toUpperCase().toCharArray()[0].toInt() - 55) * 8
            }
            val mid = card.substring(1, 7)
            val end = card.substring(7, 8)
            val chars = mid.toCharArray()
            var iflag = 7
            for (c in chars) {
                sum = sum + (c.toString() + "").toInt() * iflag
                iflag--
            }
            sum = if ("A" == end.toUpperCase()) {
                sum + 10
            } else {
                sum + end.toInt()
            }
            return sum % 11 == 0
        }

        /**
         * 将字符数组转换成数字数组
         *
         * @param ca 字符数组
         * @return 数字数组
         */
        private fun convertCharToInt(ca: CharArray): IntArray {
            val len = ca.size
            val iArr = IntArray(len)
            try {
                for (i in 0 until len) {
                    iArr[i] = ca[i].toString().toInt()
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return iArr
        }

        /**
         * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
         *
         * @param iArr
         * @return 身份证编码。
         */
        private fun getPowerSum(iArr: IntArray): Int {
            var iSum = 0
            if (power.size == iArr.size) {
                for (i in iArr.indices) {
                    for (j in power.indices) {
                        if (i == j) {
                            iSum = iSum + iArr[i] * power[j]
                        }
                    }
                }
            }
            return iSum
        }

        /**
         * 将power和值与11取模获得余数进行校验码判断
         *
         * @param iSum
         * @return 校验位
         */
        private fun getCheckCode18(iSum: Int): String {
            var sCode = ""
            when (iSum % 11) {
                10 -> sCode = "2"
                9 -> sCode = "3"
                8 -> sCode = "4"
                7 -> sCode = "5"
                6 -> sCode = "6"
                5 -> sCode = "7"
                4 -> sCode = "8"
                3 -> sCode = "9"
                2 -> sCode = "x"
                1 -> sCode = "0"
                0 -> sCode = "1"
                else -> {
                }
            }
            return sCode
        }

        /**
         * 根据出生日期获取年龄
         *
         * 规则：2岁以内使用月份表示、100天以内使用天数表示、72小时以内使用小时表示；2岁以上以周岁表示,日期之差超过150岁，则返回空。
         *
         * @param birthday 出生日期
         * @return
         */
        fun getAgeDescriptionByBirthday(birthday: String?, format: DateFormat?): String {
            return getAgeDescriptionByBirthday(DateUtils.string2Date(birthday, format))
        }

        /**
         * 根据出生日期获取年龄描述信息
         *
         * 规则：2岁以内使用月份表示、100天以内使用天数表示、72小时以内使用小时表示；2岁以上以周岁表示,日期之差超过150岁，则返回空。
         *
         * @param birthday 出生日期(yyyyMMdd)
         * @return
         */
        fun getAgeDescriptionByBirthday(birthday: Date?): String {
            var ageDescription = ""
            val cal = Calendar.getInstance()
            val yearNow = cal[Calendar.YEAR] // 当前系统的年份
            val monthNow = cal[Calendar.MONTH] + 1 // 当前系统的月份
            val dayNow = cal[Calendar.DAY_OF_MONTH]
            val millisNow = cal.timeInMillis
            cal.time = birthday
            val yearBirth = cal[Calendar.YEAR] // 出生年份
            val monthBirth = cal[Calendar.MONTH] + 1 // 出生月份
            val dayBirth = cal[Calendar.DAY_OF_MONTH]
            val millisBirth = cal.timeInMillis
            var ages = yearNow - yearBirth // 相差的年份
            val time = millisNow - millisBirth
            val days = (time / (24 * 60 * 60 * 1000)).toInt() // 相差的天数
            val hours = (time / (60 * 60 * 1000)).toInt() // 相差的时间
            if (ages > 2 && ages <= 150) { // 超过2岁，小于150岁，用岁表示
                if (monthNow <= monthBirth) {
                    if (monthNow == monthBirth) {
                        if (dayNow < dayBirth) {
                            ages--
                        }
                    } else {
                        ages--
                    }
                }
                ageDescription = ages.toString() + "岁"
            } else if (ages <= 2) { // 两周岁以内
                if (days > 100) { // 2两岁以内，多于100天，用月份表示
                    ageDescription = (ages * 12 + (monthNow - monthBirth)).toString() + "月"
                } else { // 100天以内
                    ageDescription = if (hours > 72) { // 100天内，多余72小时
                        days.toString() + "天"
                    } else {
                        hours.toString() + "时"
                    }
                }
            }
            return ageDescription
        }

        /**
         * 根据身份编号获取年龄描述信息
         *
         * 规则：2岁以内使用月份表示、100天以内使用天数表示、72小时以内使用小时表示；2岁以上以周岁表示,日期之差超过150岁，则返回空。
         *
         * @param idCard 身份编号
         * @return
         */
        fun getAgeDescriptionByIdCard(idCard: String?): String {
            return getAgeDescriptionByBirthday(
                getBirthByIdCard(idCard),
                DateUtils.yyyyMMddNoSep.get()
            )
        }

        /**
         * 根据身份编号获取生日
         *
         * @param idCard 身份编号
         * @return 生日(yyyyMMdd)
         */
        fun getBirthByIdCard(idCard: String?): String? {
            var idCard = idCard
            val len = idCard!!.length
            if (len < CHINA_ID_MIN_LENGTH) {
                return null
            } else if (len == CHINA_ID_MIN_LENGTH) {
                idCard = convert15CardTo18(idCard)
            }
            return idCard!!.substring(6, 14)
        }

        /**
         * 根据身份编号获取生日年
         *
         * @param idCard 身份编号
         * @return 生日(yyyy)
         */
        fun getYearByIdCard(idCard: String?): Short? {
            var idCard = idCard
            val len = idCard!!.length
            if (len < CHINA_ID_MIN_LENGTH) {
                return null
            } else if (len == CHINA_ID_MIN_LENGTH) {
                idCard = convert15CardTo18(idCard)
            }
            return idCard!!.substring(6, 10).toShort()
        }

        /**
         * 根据身份编号获取生日月
         *
         * @param idCard 身份编号
         * @return 生日(MM)
         */
        fun getMonthByIdCard(idCard: String?): Short? {
            var idCard = idCard
            val len = idCard!!.length
            if (len < CHINA_ID_MIN_LENGTH) {
                return null
            } else if (len == CHINA_ID_MIN_LENGTH) {
                idCard = convert15CardTo18(idCard)
            }
            return idCard!!.substring(10, 12).toShort()
        }

        /**
         * 根据身份编号获取生日天
         *
         * @param idCard 身份编号
         * @return 生日(dd)
         */
        fun getDayByIdCard(idCard: String?): Short? {
            var idCard = idCard
            val len = idCard!!.length
            if (len < CHINA_ID_MIN_LENGTH) {
                return null
            } else if (len == CHINA_ID_MIN_LENGTH) {
                idCard = convert15CardTo18(idCard)
            }
            return idCard!!.substring(12, 14).toShort()
        }

        /**
         * 根据身份编号获取性别
         *
         * @param idCard 身份编号
         * @return 性别(M - 男 ， F - 女 ， N - 未知)
         */
        fun getGenderByIdCard(idCard: String?): String {
            var idCard = idCard
            var sGender = "N"
            if (idCard!!.length == CHINA_ID_MIN_LENGTH) {
                idCard = convert15CardTo18(idCard)
            }
            val sCardNum = idCard!!.substring(16, 17)
            sGender = if (sCardNum.toInt() % 2 != 0) {
                "M"
            } else {
                "F"
            }
            return sGender
        }

        /**
         * 根据身份编号获取户籍省份
         *
         * @param idCard 身份编码
         * @return 省级编码。
         */
        fun getProvinceByIdCard(idCard: String): String? {
            val len = idCard.length
            var sProvince: String? = null
            var sProvinNum = ""
            if (len == CHINA_ID_MIN_LENGTH || len == CHINA_ID_MAX_LENGTH) {
                sProvinNum = idCard.substring(0, 2)
            }
            sProvince = cityCodes[sProvinNum]
            return sProvince
        }

        /**
         * 数字验证
         *
         * @param val
         * @return 提取的数字。
         */
        private fun isNum(`val`: String?): Boolean {
            return !(`val` == null || "" == `val`) && `val`.matches(Regex("^[0-9]*$"))
        }

        /**
         * 验证小于当前日期 是否有效
         *
         * @param iYear  待验证日期(年)
         * @param iMonth 待验证日期(月 1-12)
         * @param iDate  待验证日期(日)
         * @return 是否有效
         */
        private fun validate(iYear: Int, iMonth: Int, iDate: Int): Boolean {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val datePerMonth: Int
            if (iYear < MIN || iYear >= year) {
                return false
            }
            if (iMonth < 1 || iMonth > 12) {
                return false
            }
            datePerMonth = when (iMonth) {
                4, 6, 9, 11 -> 30
                2 -> {
                    val dm =
                        (iYear % 4 == 0 && iYear % 100 != 0 || iYear % 400 == 0) && iYear > MIN && iYear < year
                    if (dm) 29 else 28
                }
                else -> 31
            }
            return iDate >= 1 && iDate <= datePerMonth
        }

        /**
         * 验证身份证是否合法
         *
         * @param idCard 身份证卡号
         * @return
         */
        fun validateCard(idCard: String): Boolean {
            val card = idCard.trim { it <= ' ' }
            if (validateIdCard18(card)) {
                return true
            }
            if (validateIdCard15(card)) {
                return true
            }
            val cardval = validateIdCard10(card)
            return cardval != null && "true" == cardval[2]
        }

        /**
         * 隐藏身份证信息
         *
         * @param idCardNo ：要隐藏的身份证信息
         * @return 隐藏一部分信息后的身份证号
         */
        fun hideIdCard(idCardNo: String): String {
            var idCardNo = idCardNo
            if (StringUtils.Companion.isEmpty(idCardNo)) {
                return ""
            }
            if (idCardNo.length >= 7) {
                val tempStr = idCardNo.substring(3, idCardNo.length - 4)
                idCardNo = idCardNo.replace(tempStr.toRegex(), "*****")
            }
            return idCardNo
        }

        init {
            cityCodes["11"] = "北京"
            cityCodes["12"] = "天津"
            cityCodes["13"] = "河北"
            cityCodes["14"] = "山西"
            cityCodes["15"] = "内蒙古"
            cityCodes["21"] = "辽宁"
            cityCodes["22"] = "吉林"
            cityCodes["23"] = "黑龙江"
            cityCodes["31"] = "上海"
            cityCodes["32"] = "江苏"
            cityCodes["33"] = "浙江"
            cityCodes["34"] = "安徽"
            cityCodes["35"] = "福建"
            cityCodes["36"] = "江西"
            cityCodes["37"] = "山东"
            cityCodes["41"] = "河南"
            cityCodes["42"] = "湖北"
            cityCodes["43"] = "湖南"
            cityCodes["44"] = "广东"
            cityCodes["45"] = "广西"
            cityCodes["46"] = "海南"
            cityCodes["50"] = "重庆"
            cityCodes["51"] = "四川"
            cityCodes["52"] = "贵州"
            cityCodes["53"] = "云南"
            cityCodes["54"] = "西藏"
            cityCodes["61"] = "陕西"
            cityCodes["62"] = "甘肃"
            cityCodes["63"] = "青海"
            cityCodes["64"] = "宁夏"
            cityCodes["65"] = "新疆"
            cityCodes["71"] = "台湾"
            cityCodes["81"] = "香港"
            cityCodes["82"] = "澳门"
            cityCodes["91"] = "国外"
            twFirstCode["A"] = 10
            twFirstCode["B"] = 11
            twFirstCode["C"] = 12
            twFirstCode["D"] = 13
            twFirstCode["E"] = 14
            twFirstCode["F"] = 15
            twFirstCode["G"] = 16
            twFirstCode["H"] = 17
            twFirstCode["J"] = 18
            twFirstCode["K"] = 19
            twFirstCode["L"] = 20
            twFirstCode["M"] = 21
            twFirstCode["N"] = 22
            twFirstCode["P"] = 23
            twFirstCode["Q"] = 24
            twFirstCode["R"] = 25
            twFirstCode["S"] = 26
            twFirstCode["T"] = 27
            twFirstCode["U"] = 28
            twFirstCode["V"] = 29
            twFirstCode["X"] = 30
            twFirstCode["Y"] = 31
            twFirstCode["W"] = 32
            twFirstCode["Z"] = 33
            twFirstCode["I"] = 34
            twFirstCode["O"] = 35
            hkFirstCode["A"] = 1
            hkFirstCode["B"] = 2
            hkFirstCode["C"] = 3
            hkFirstCode["R"] = 18
            hkFirstCode["U"] = 21
            hkFirstCode["Z"] = 26
            hkFirstCode["X"] = 24
            hkFirstCode["W"] = 23
            hkFirstCode["O"] = 15
            hkFirstCode["N"] = 14
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}