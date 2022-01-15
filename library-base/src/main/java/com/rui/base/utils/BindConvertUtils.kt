package com.rui.base.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.ParseException


/**
 *
 *
 * *******************************
 * *@Author
 * *date ：
 * *description:bind数据转换工具类
 * *******************************
 */

object BindConvertUtils {

    @JvmStatic
    fun int2Str(i: Int?): String =
        i?.toString() ?: "0"

    @JvmStatic
    fun float2Str(f: Float?): String {
        if (f == null) return "0"
        return if (f - f.toInt() == 0f) f.toInt().toString()
        else f.toString()
    }

    @JvmStatic
    fun int2DBStr(i: Int?): String {
        if (i == null) return ""
        var d = i.toDouble() / 100.0
        return double2Str(d)
    }

    @JvmStatic
    fun double2Str(d: Double?): String {
        if (d == null) return "0"
        return if (d - d.toInt() == 0.0) {
            d.toInt().toString()
        } else d.toString()
    }

    @JvmStatic
    fun numToMoreStr(i: String?): String {
        if (i == null) {
            return "0"
        } else if (i.toInt() < 9999) {
            return i
        } else if (i.toInt() > 10000000) {
            return "1000万+"
        } else {
            val str = BigDecimal(i).divide(BigDecimal(10000), 1, RoundingMode.HALF_UP).toString()
            if (str.endsWith(".0")) {
                return str.replace("0.00", "") + "万"
            } else {
                return str + "万"
            }

        }
    }

    @JvmStatic
    fun formatCommentTime(timeStr: String?): String {
        if (timeStr.isNullOrEmpty())
            return "刚刚"
        return try {
            TimeFormater.formatCommentTime(timeStr)
        } catch (e: ParseException) {
            timeStr
        }
    }


    @JvmStatic
    fun hideMobile(str: String): String {
        return if (str.length < 11)
            str
        else {
            str.substring(0, 3) + "****" + str.substring(7, str.length)
        }

    }


}

