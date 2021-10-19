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

import java.math.BigDecimal

/**
 * 资金运算工具类
 *
 * @author zjr
 * @since 2018/6/27 下午2:50
 */
class BigDecimalUtils private constructor() {
    companion object {
        private const val DEF_DIV_SCALE = 10

        /**
         * 提供精确的加法运算
         *
         * @param v1 被加数
         * @param v2 加数
         * @return 两个参数的和
         */
        fun add(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.add(b2).toDouble()
        }

        /**
         * 提供精确的减法运算
         *
         * @param v1 被减数
         * @param v2 减数
         * @return 两个参数的差
         */
        fun substract(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.subtract(b2).toDouble()
        }

        /**
         * 提供精确的乘法运算
         *
         * @param v1 被乘数
         * @param v2 乘数
         * @return 两个参数的积
         */
        fun multiply(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.multiply(b2).toDouble()
        }
        /**
         * 提供（相对）精确的除法运算.
         * 当发生除不尽的情况时,由scale参数指 定精度,以后的数字四舍五入.
         *
         * @param v1    被除数
         * @param v2    除数
         * @param scale 表示需要精确到小数点以后几位
         * @return 两个参数的商
         */
        /**
         * 提供（相对）精确的除法运算,当发生除不尽的情况时,
         * 精确到小数点以后10位,以后的数字四舍五入.
         *
         * @param v1 被除数
         * @param v2 除数
         * @return 两个参数的商
         */
        @JvmOverloads
        fun divide(v1: Double, v2: Double, scale: Int = DEF_DIV_SCALE): Double {
            require(scale >= 0) { "The scale must be a positive integer or zero" }
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        }

        /**
         * 提供精确的小数位四舍五入处理
         *
         * @param v     需要四舍五入的数字
         * @param scale 小数点后保留几位
         * @return 四舍五入后的结果
         */
        fun round(v: Double, scale: Int): Double {
            require(scale >= 0) { "The scale must be a positive integer or zero" }
            val b = BigDecimal(java.lang.Double.toString(v))
            val one = BigDecimal("1")
            return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        }

        /**
         * 四舍五入金额，每三位以逗号隔开
         *
         * @param s
         * @return
         */
        fun splitAndFormatMoney(s: BigDecimal?): String {
            return formatMoney(s, ",")
        }
        /**
         * 四舍五入金额
         *
         * @param s
         * @return
         */
        /**
         * 四舍五入金额
         *
         * @param s
         * @return
         */
        @JvmOverloads
        fun formatMoney(s: BigDecimal?, separator: String? = ""): String {
            var s = s
            var retVal: String
            val str: String
            val is_positive_integer: Boolean
            if (null == s) {
                return "0.00"
            }
            if (0.0 == s.toDouble()) {
                return "0.00"
            }
            //判断是否正整数
            is_positive_integer = s.toString().contains("-")
            //是负整数
            if (is_positive_integer) {
                //去掉 - 号
                s = BigDecimal(s.toString().substring(1, s.toString().length))
            }
            str = s.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            val sb = StringBuffer()
            val strs = str.split("\\.").toTypedArray()
            var j = 1
            for (i in 0 until strs[0].length) {
                val a = strs[0][strs[0].length - i - 1]
                sb.append(a)
                if (j % 3 == 0 && i != strs[0].length - 1) {
                    sb.append(separator)
                }
                j++
            }
            val str1 = sb.toString()
            val sb1 = StringBuffer()
            for (i in 0 until str1.length) {
                val a = str1[str1.length - 1 - i]
                sb1.append(a)
            }
            sb1.append(".")
            sb1.append(strs[1])
            retVal = sb1.toString()
            if (is_positive_integer) {
                retVal = "-$retVal"
            }
            return retVal
        }

        /**
         * 比较数值
         *
         * @param amount  输入的数值
         * @param compare 被比较的数字
         * @return true 大于被比较的数
         */
        fun compareBigDecimal(amount: String?, compare: Double): Boolean {
            val bigDecimal = BigDecimal(amount)
            return bigDecimal.compareTo(BigDecimal.valueOf(compare)) != -1
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}