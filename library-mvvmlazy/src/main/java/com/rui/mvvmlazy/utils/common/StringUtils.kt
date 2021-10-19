package com.rui.mvvmlazy.utils.common

import java.io.PrintWriter
import java.io.StringWriter
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by zjr on 2020/5/14.
 * 字符串相关工具类
 */
class StringUtils() {
    companion object {
        /**
         * 判断字符串是否为null或长度为0
         *
         * @param s 待校验字符串
         * @return `true`: 空<br></br> `false`: 不为空
         */
        fun isEmpty(s: CharSequence?): Boolean {
            return s == null || s.isEmpty()
        }

        /**
         * 判断字符串是否为 null 或全为空格
         *
         * @param s 待校验字符串
         * @return `true`: null 或全空格<br></br> `false`: 不为 null 且不全空格
         */
        fun isEmptyTrim(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.isEmpty()
        }

        /**
         * 判断字符串是否为null或全为空白字符
         *
         * @param s 待校验字符串
         * @return `true`: null或全空白字符<br></br> `false`: 不为null且不全空白字符
         */
        @JvmStatic
        fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        /**
         * 判断两字符串是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        fun equals(a: CharSequence?, b: CharSequence?): Boolean {
            if (a === b) return true
            var length = 0
            return if (a != null && b != null && a.length.also { length = it } == b.length) {
                if (a is String && b is String) {
                    a == b
                } else {
                    for (i in 0 until length) {
                        if (a[i] != b[i]) return false
                    }
                    true
                }
            } else false
        }

        /**
         * 判断两字符串忽略大小写是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        fun equalsIgnoreCase(a: String?, b: String?): Boolean {
            return a?.equals(b, ignoreCase = true) ?: (b == null)
        }

        /**
         * null转为长度为0的字符串
         *
         * @param s 待转字符串
         * @return s为null转为长度为0字符串，否则不改变
         */
        fun null2Length0(s: String?): String {
            return s ?: ""
        }

        /**
         * 返回字符串长度
         *
         * @param s 字符串
         * @return null返回0，其他返回自身长度
         */
        fun length(s: CharSequence?): Int {
            return s?.length ?: 0
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        fun upperFirstLetter(s: String): String {
            if (isEmpty(s) || !Character.isLowerCase(
                    s[0]
                )
            ) return s
            return (s[0].code - 32).toString() + s.substring(1)
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        fun lowerFirstLetter(s: String): String {
            if (isEmpty(s) || !Character.isUpperCase(
                    s[0]
                )
            ) return s
            return (s[0].code + 32).toString() + s.substring(1)
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        fun reverse(s: String): String {
            val len = length(s)
            if (len <= 1) return s
            val mid = len shr 1
            val chars = s.toCharArray()
            var c: Char
            for (i in 0 until mid) {
                c = chars[i]
                chars[i] = chars[len - i - 1]
                chars[len - i - 1] = c
            }
            return String(chars)
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        fun toDBC(s: String): String {
            if (isEmpty(s)) return s
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i].code == 12288) {
                    chars[i] = ' '
                } else if (chars[i].code in 65281..65374) {
                    chars[i] = (chars[i] - 65248)
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        fun toSBC(s: String): String {
            if (isEmpty(s)) return s
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i] == ' ') {
                    chars[i] = 12288.toChar()
                } else if (chars[i].code in 33..126) {
                    chars[i] = (chars[i] + 65248)
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 获取String内容
         *
         * @param s
         * @return
         */
        fun getString(s: String?): String {
            return if (isEmptyTrim(s)) "" else s!!
        }

        /**
         * 获取String内容，去除前后空格
         *
         * @param s
         * @return
         */
        fun getStringTrim(s: String?): String {
            return if (isEmptyTrim(s)) "" else s!!.trim { it <= ' ' }
        }

        /**
         * 获取String内容，去除所有空格
         *
         * @param s
         * @return
         */
        fun getStringNoSpace(s: String?): String {
            return if (isEmptyTrim(s)) "" else replaceBlank(s)
        }

        /**
         * 裁剪字符串
         *
         * @param originalStr 原字符串
         * @param beginIndex  开始的索引
         * @param endIndex    结束的索引
         * @return
         */
        fun cutString(originalStr: String, beginIndex: Int, endIndex: Int): String {
            return if (isEmpty(originalStr)) {
                originalStr
            } else {
                try {
                    originalStr.substring(beginIndex, endIndex)
                } catch (e: IndexOutOfBoundsException) {
                    originalStr
                }
            }
        }
        /**
         * String转Int（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */
        /**
         * String转Int（防止崩溃）
         *
         * @param value
         * @return
         */
        @JvmOverloads
        fun toInt(value: String, defValue: Int = 0): Int {
            return try {
                value.toInt()
            } catch (e: NumberFormatException) {
                defValue
            }
        }
        /**
         * String转Float（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */

        @JvmOverloads
        fun toFloat(value: String, defValue: Float = 0f): Float {
            return try {
                value.toFloat()
            } catch (e: NumberFormatException) {
                defValue
            }
        }
        /**
         * String转Short（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */

        @JvmOverloads
        fun toShort(value: String, defValue: Short = 0.toShort()): Short {
            return try {
                value.toShort()
            } catch (e: NumberFormatException) {
                defValue
            }
        }
        /**
         * String转Long（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */
        @JvmOverloads
        fun toLong(value: String, defValue: Long = 0): Long {
            return try {
                value.toLong()
            } catch (e: NumberFormatException) {
                defValue
            }
        }
        /**
         * String转Double（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */
        @JvmOverloads
        fun toDouble(value: String, defValue: Double = 0.0): Double {
            return try {
                value.toDouble()
            } catch (e: NumberFormatException) {
                defValue
            }
        }
        /**
         * String转Boolean（防止崩溃）
         *
         * @param value
         * @param defValue 默认值
         * @return
         */

        @JvmOverloads
        fun toBoolean(value: String?, defValue: Boolean = false): Boolean {
            return try {
                java.lang.Boolean.parseBoolean(value)
            } catch (e: Exception) {
                defValue
            }
        }

        /**
         * 判断字符串是否是整数
         */
        fun isInteger(value: String): Boolean {
            return try {
                value.toInt()
                true
            } catch (e: NumberFormatException) {
                false
            }
        }

        /**
         * 判断字符串是否是双精度浮点数
         */
        fun isDouble(value: String): Boolean {
            return try {
                value.toDouble()
                value.contains(".")
            } catch (e: NumberFormatException) {
                false
            }
        }

        /**
         * 判断字符串是否是数字
         */
        fun isNumber(value: String): Boolean {
            return isInteger(value) || isDouble(value)
        }

        /**
         * 获取异常栈信息，不同于Log.getStackTraceString()，该方法不会过滤掉UnknownHostException.
         *
         * @param t [Throwable]
         * @return 异常栈里的信息
         */
        fun getStackTraceString(t: Throwable): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }

        /**
         * 字符串连接，将参数列表拼接为一个字符串
         *
         * @param more 追加
         * @return 返回拼接后的字符串
         */
        fun concat(vararg more: Any?): String {
            return concatSpiltWith("", *more)
        }

        /**
         * 字符串连接，将参数列表通过分隔符拼接为一个字符串
         *
         * @param split
         * @param more
         * @return 回拼接后的字符串
         */
        fun concatSpiltWith(split: String?, vararg more: Any?): String {
            val buf = StringBuilder()
            for (i in 0 until more.size) {
                if (i != 0) {
                    buf.append(split)
                }
                buf.append(toString(more[i]))
            }
            return buf.toString()
        }

        /**
         * 判断一个数组里是否包含指定对象
         *
         * @param array 对象数组
         * @param obj   要判断的对象
         * @return 是否包含
         */
        fun contains(array: Array<Any?>?, vararg obj: Any?): Boolean {
            return if (array == null || obj == null || array.size == 0) {
                false
            } else Arrays.asList(*array)
                .containsAll(Arrays.asList(*obj))
        }

        /**
         * 将对象转化为String
         *
         * @param object
         * @return
         */
        fun toString(`object`: Any?): String {
            if (`object` == null) {
                return "null"
            }
            if (!`object`.javaClass.isArray) {
                return `object`.toString()
            }
            if (`object` is BooleanArray) {
                return Arrays.toString(`object` as BooleanArray?)
            }
            if (`object` is ByteArray) {
                return Arrays.toString(`object` as ByteArray?)
            }
            if (`object` is CharArray) {
                return Arrays.toString(`object` as CharArray?)
            }
            if (`object` is ShortArray) {
                return Arrays.toString(`object` as ShortArray?)
            }
            if (`object` is IntArray) {
                return Arrays.toString(`object` as IntArray?)
            }
            if (`object` is LongArray) {
                return Arrays.toString(`object` as LongArray?)
            }
            if (`object` is FloatArray) {
                return Arrays.toString(`object` as FloatArray?)
            }
            if (`object` is DoubleArray) {
                return Arrays.toString(`object` as DoubleArray?)
            }
            return if (`object` is Array<*>) {
                Arrays.deepToString(`object` as Array<Any?>?)
            } else "Couldn't find a correct type for the object"
        }

        /**
         * 过滤字符串中所有的特殊字符
         *
         * @param str
         * @return
         */
        fun replaceSpecialCharacter(str: String?): String {
            var dest = ""
            if (str != null) {
                val regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
                val p = Pattern.compile(regEx)
                val m = p.matcher(str)
                dest = m.replaceAll("").trim { it <= ' ' }
            }
            return dest
        }

        /**
         * 过滤字符串中的[和]
         *
         * @param str
         * @return
         */
        fun replaceBracket(str: String?): String {
            var dest = ""
            if (str != null) {
                val regEx = "[\\[\\]]"
                val p = Pattern.compile(regEx)
                val m = p.matcher(str)
                dest = m.replaceAll("").trim { it <= ' ' }
            }
            return dest
        }

        /**
         * 过滤字符串中的空格
         *
         * @param str
         * @return
         */
        fun replaceBlank(str: String?): String {
            var dest = ""
            if (str != null) {
                val p = Pattern.compile("\\s*|\t|\r|\n")
                val m = p.matcher(str)
                dest = m.replaceAll("")
            }
            return dest
        }

        /**
         * 根据分隔符将String转换为List
         *
         *
         * 例如:aa,bb,cc --> {"aa","bb","cc"}
         *
         * @param str
         * @param separator 分隔符
         * @return
         */
        fun stringToList(str: String, separator: String?): List<String> {
            return Arrays.asList(*str.split(separator!!).toTypedArray())
        }

        /**
         * 获取对象的类名
         *
         * @param object
         * @return
         */
        fun getSimpleName(`object`: Any?): String {
            return if (`object` != null) `object`.javaClass.simpleName else "NULL"
        }

        /**
         * 获取对象的类名
         *
         * @param object
         * @return
         */
        fun getName(`object`: Any?): String {
            return if (`object` != null) `object`.javaClass.name else "NULL"
        }

        /**
         * 将字符串格式化为带两位小数的字符串
         *
         * @param str 字符串
         * @return
         */
        fun format2Decimals(str: String): String {
            // 构造方法的字符格式这里如果小数不足2位,会以0补足.
            val decimalFormat = DecimalFormat("0.00")
            return if (isEmpty(str)) "" else decimalFormat.format(toDouble(str, -1.0))
        }

        /**
         * 将浮点数转化为带两位小数的字符串
         *
         * @param number 字符串
         * @return
         */
        fun format2Decimals(number: Double): String {
            // 构造方法的字符格式这里如果小数不足2位,会以0补足.
            return DecimalFormat("0.00").format(number)
        }

        /**
         * 将浮点数转化为带两位小数的字符串
         *
         * @param number 字符串
         * @return
         */
        fun format2Decimals(number: Float): String {
            // 构造方法的字符格式这里如果小数不足2位,会以0补足.
            return DecimalFormat("0.00").format(number.toDouble())
        }

        /**
         * 比较两个版本号
         *
         * @param versionName1 比较版本1
         * @param versionName2 比较版本2
         * @return [> 0 versionName1 > versionName2] [= 0 versionName1 = versionName2]  [< 0 versionName1 < versionName2]
         */
        fun compareVersionName(versionName1: String, versionName2: String): Int {
            if (versionName1 == versionName2) {
                return 0
            }
            //注意此处为正则匹配，不能用"."；
            val versionArray1 = versionName1.split("\\.").toTypedArray()
            val versionArray2 = versionName2.split("\\.").toTypedArray()
            var idx = 0
            //取最小长度值
            val minLength = Math.min(versionArray1.size, versionArray2.size)
            var diff = 0
            while (idx < minLength //先比较长度
                && versionArray1[idx].length - versionArray2[idx].length.also {
                    diff = it
                } == 0 //再比较字符
                && versionArray1[idx].compareTo(versionArray2[idx]).also { diff = it } == 0
            ) {
                ++idx
            }
            //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
            diff = if (diff != 0) diff else versionArray1.size - versionArray2.size
            return diff
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}