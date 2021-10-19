package com.rui.base.utils


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间格式化工具类
 */
 public object TimeFormater {

    var DATE_FORMAT_DEF = "yyyy-MM-dd HH:mm"
    var DATE_FORMAT_CN = "yyyy年MM月dd日 HH时mm分"
    var DATE_FORMAT_CN_DATE = "yyyy年MM月dd日"

    /**
     * 格式化毫秒数为 xx:xx:xx这样的时间格式。
     *
     * @param ms 毫秒数
     * @return 格式化后的字符串
     */
    fun formatMs(ms: Long): String {
        return formatS((ms / 1000).toInt())
    }

    /**
     * 格式化秒数为 xx:xx:xx这样的时间格式。
     *
     * @param seconds 秒数
     * @return 格式化后的字符串
     */
    @JvmStatic
    fun formatS(seconds: Int): String {
        val finalSec = seconds % 60
        val finalMin = seconds / 60 % 60
        val finalHour = seconds / 3600

        val msBuilder = StringBuilder("")
        if (finalHour > 9) {
            msBuilder.append(finalHour).append(":")
        } else if (finalHour > 0) {
            msBuilder.append("0").append(finalHour).append(":")
        } else {
            msBuilder.append("00").append(":")
        }

        if (finalMin > 9) {
            msBuilder.append(finalMin).append(":")
        } else if (finalMin > 0) {
            msBuilder.append("0").append(finalMin).append(":")
        } else {
            msBuilder.append("00").append(":")
        }

        if (finalSec > 9) {
            msBuilder.append(finalSec)
        } else if (finalSec > 0) {
            msBuilder.append("0").append(finalSec)
        } else {
            msBuilder.append("00")
        }

        return msBuilder.toString()
    }


    @Throws(ParseException::class)
    fun formatCommentTime(timeStr: String): String {

        val sdf = SimpleDateFormat(DATE_FORMAT_DEF, Locale.getDefault())
        val time = sdf.parse(timeStr)
        val curTime = System.currentTimeMillis()
        val duration = curTime - time.time
        return when {
            duration < 10L * 1000 -> "刚刚"
            duration < 60L * 1000 -> "${duration / 1000}秒前"
            duration < 1000L * 60 * 60 -> "${duration / 1000 / 60}分钟前"
            duration < 1000L * 60 * 60 * 24 -> "${duration / 1000 / 60 / 60}小时前"
            duration < 1000L * 60 * 60 * 24 * 2 -> "昨天 ${timeStr.split(" ")[1]}"
            duration < 1000L * 60 * 60 * 24 * 3 -> "前天 ${timeStr.split(" ")[1]}"
            duration < 1000L * 60 * 60 * 24 * 5 -> "${duration / 1000 / 60 / 60 / 24}天前"
            else -> {
                timeStr
            }
        }
    }

    //格式化字符串
    @JvmStatic
    fun format(fFormat: String, toFormat: String, timeStr: String?): String {
        if (timeStr == null) return ""
        return try {
            SimpleDateFormat(toFormat, Locale.getDefault()).format(
                    SimpleDateFormat(fFormat, Locale.getDefault()).parse(
                            timeStr
                    )
            )
        } catch (e: ParseException) {
            ""
        }
    }

    //格式化日期
    @JvmStatic
    fun formatDate(timeStr: String?): String {
        return format("yyyy-MM-dd", "yyyy年MM月dd日", timeStr)
    }

    fun format(date: Date?, tFormat: String): String {
        if (date == null) return ""
        return try {
            SimpleDateFormat(tFormat, Locale.getDefault()).format(date)
        } catch (e: ParseException) {
            ""
        }
    }

    //天数差
    @JvmStatic
    fun dateDiff(stDate: String, endDate: String, format: String): Int {
        val formater = SimpleDateFormat(format, Locale.getDefault())
        return try {
            TimeFormater.dateDiff(formater.parse(endDate), formater.parse(stDate))
        } catch (e: ParseException) {
            1
        }
    }

    //天数差
    @JvmStatic
    fun dateDiff(stDate: Date?, endDate: Date?): Int {
        if (stDate == null || endDate == null) return 0
        return ((endDate.time - stDate.time) / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * 日期转换星期
     */
    @JvmStatic
    fun dateToWeek(datetime: String, formatStr: String): String {
        val f = SimpleDateFormat(formatStr)
        val weekDays = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        val cal = Calendar.getInstance() // 获得一个日历
        var datet: Date? = null
        try {
            datet = f.parse(datetime)
            cal.time = datet
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var w = cal.get(Calendar.DAY_OF_WEEK) - 1 // 指示一个星期中的某天。
        if (w < 0)
            w = 0
        return weekDays[w]
    }

    /**
     *  *功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @param formatStr
     * @return
     * long
     */
    @JvmStatic
    fun getDaySub(beginDateStr: String, endDateStr: String, formatStr: String): Long {
        var day: Long = 0
        val format = SimpleDateFormat(formatStr)
        val beginDate: Date
        val endDate: Date
        try {
            beginDate = format.parse(beginDateStr)
            endDate = format.parse(endDateStr)
            day = (endDate.time - beginDate.time) / (24 * 60 * 60 * 1000)
            //System.out.println("相隔的天数="+day);
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return day
    }

    /*获取毫秒差*/
    @JvmStatic
     fun getMillisSub(endDateStr: String): Long {
        var millis: Long = 0
        val format = SimpleDateFormat(if (endDateStr.length > 10) {
            "yyyy-MM-dd HH:mm:ss"
        } else {
            "yyyy-MM-dd"
        })
        val endDate: Date
        try {
            endDate = format.parse(endDateStr)
            millis = endDate.time - Date().time
            //System.out.println("相隔的天数="+day);
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return millis
    }

    //时间字符串转换
    @JvmStatic
    fun strToDate(timeStr: String?, formatStr: String): Date? {
        if (timeStr == null) return null
        val format = SimpleDateFormat(formatStr, Locale.getDefault())
        return try {
            format.parse(timeStr)
        } catch (e: ParseException) {
            null
        }
    }
}
