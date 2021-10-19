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
package com.rui.mvvmlazy.utils.data

import android.annotation.SuppressLint
import com.rui.mvvmlazy.utils.common.StringUtils.Companion.isSpace
import com.rui.mvvmlazy.utils.constant.DateFormatConstants
import com.rui.mvvmlazy.utils.constant.TimeConstants
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * <pre>
 * desc   : 日期工具
 * author : zjr
 * time   : 2018/4/30 下午12:22
</pre> *
 */
class DateUtils() {
    companion object {
        /**
         * yyyy-MM-dd
         */
        val yyyyMMdd: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMdd)
            }
        }

        /**
         * yyyyMMdd
         */
        val yyyyMMddNoSep: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMddNoSep)
            }
        }

        /**
         * HH:mm:ss
         */
        val HHmmss: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.HHmmss)
            }
        }

        /**
         * HH:mm
         */
        val HHmm: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.HHmm)
            }
        }

        /**
         * yyyy-MM-dd HH:mm:ss
         */
        val yyyyMMddHHmmss: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMddHHmmss)
            }
        }

        /**
         * yyyyMMddHHmmss
         */
        val yyyyMMddHHmmssNoSep: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMddHHmmssNoSep)
            }
        }

        /**
         * yyyy-MM-dd HH:mm
         */
        val yyyyMMddHHmm: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMddHHmm)
            }
        }

        /**
         * yyyy-MM-dd HH:mm:ss:SSS
         */
        val yyyyMMddHHmmssSSS: ThreadLocal<DateFormat> = object : ThreadLocal<DateFormat>() {
            @SuppressLint("SimpleDateFormat")
            override fun initialValue(): DateFormat {
                return SimpleDateFormat(DateFormatConstants.yyyyMMddHHmmssSSS)
            }
        }

        /**
         * 将时间戳转为时间字符串
         *
         * 格式为 format
         *
         * @param millis 毫秒时间戳
         * @param format 时间格式
         * @return 时间字符串
         */
        fun millis2String(millis: Long, format: DateFormat?): String {
            return date2String(Date(millis), format)
        }

        /**
         * 将 Date 类型转为时间字符串
         *
         * 格式为 format
         *
         * @param date   Date 类型时间
         * @param format 时间格式
         * @return 时间字符串
         */
        fun date2String(date: Date?, format: DateFormat?): String {
            return if (format != null) {
                format.format(date)
            } else {
                ""
            }
        }

        /**
         * 将时间字符串转为时间戳
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 毫秒时间戳
         */
        fun string2Millis(time: String?, format: DateFormat?): Long {
            try {
                if (format != null) {
                    return format.parse(time).time
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return -1
        }

        /**
         * 将时间字符串转为 Date 类型
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return Date 类型
         */
        fun string2Date(time: String?, format: DateFormat?): Date? {
            try {
                if (format != null) {
                    return format.parse(time)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 将 Date 类型转为时间戳
         *
         * @param date Date 类型时间
         * @return 毫秒时间戳
         */
        fun date2Millis(date: Date?): Long {
            return date?.time ?: -1
        }

        /**
         * 将时间戳转为 Date 类型
         *
         * @param millis 毫秒时间戳
         * @return Date 类型时间
         */
        fun millis2Date(millis: Long): Date {
            return Date(millis)
        }

        /**
         * 转换日期格式 oldFormat ---> newFormat
         *
         * @param time      时间字符串
         * @param oldFormat 旧格式
         * @param newFormat 新格式
         * @return 转换成功：新时间格式，转换失败：{}
         */
        fun translateDateFormat(
            time: String?,
            oldFormat: DateFormat?,
            newFormat: DateFormat?
        ): String {
            if (isSpace(time)) {
                return ""
            }
            val date = string2Date(time, oldFormat) //String -> Date
            return if (date != null) date2String(date, newFormat) else "" //Date -> String
        }

        /**
         * 转换日期格式   oldFormatType --->  newFormatType
         *
         * @param time
         * @param oldFormatType 旧格式
         * @param newFormatType 新格式
         * @return 转换成功：新时间格式，转换失败：
         */
        fun translateDateFormat(
            time: String?,
            oldFormatType: String?,
            newFormatType: String?
        ): String {
            return translateDateFormat(
                time,
                SimpleDateFormat(oldFormatType),
                SimpleDateFormat(newFormatType)
            )
        }

        /**
         * 判断时间字符串的格式是否正确
         *
         * @param time   时间字符串
         * @param format 时间的格式
         * @return
         */
        fun isDateFormatRight(time: String, format: DateFormat?): Boolean {
            //内容和格式出错，直接返回false
            return if (isSpace(time) || format == null) {
                false
            } else try {
                val date = format.parse(time)
                val s = format.format(date)
                time == s
            } catch (e: ParseException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 将时间字符串转换为文件名称
         *
         * @param dateTime 时间字符串
         * @param suffix   文件名后缀
         * @return
         */
        fun convertTimeToFileName(dateTime: String?, suffix: String): String? {
            if (isSpace(dateTime)) {
                return null
            }
            val p = Pattern.compile("[^\\d]+")
            val m = p.matcher(dateTime)
            return m.replaceAll("").trim { it <= ' ' } + suffix
        }
        //===============================时间计算==================================//
        /**
         * 获取日期当天的最早时间
         *
         *
         * 例如：今天日期是 2018-4-24 12:34:58，结果就是 2018-4-24 00:00:00:000
         *
         * @param date 日期
         * @return
         */
        fun getStartOfDay(date: Date?): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.time
        }

        /**
         * 获取n天后最早的时间
         *
         *
         * 例如：今天日期是 2018-4-24 12:34:58，n=2，结果就是 2018-4-26 00:00:00:000
         *
         * @param date     日期
         * @param dayAfter 几天后
         * @return
         */
        fun getStartOfDay(date: Date?, dayAfter: Int): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DAY_OF_MONTH, dayAfter)
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.time
        }

        /**
         * 获取日期当天的最后时间
         *
         *
         * 例如：今天日期是 2018-4-24 12:34:58，结果就是 2018-4-24 23:59:59:999
         *
         * @param date 日期
         * @return
         */
        fun getEndOfDay(date: Date?): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal[Calendar.HOUR_OF_DAY] = 23
            cal[Calendar.MINUTE] = 59
            cal[Calendar.SECOND] = 59
            cal[Calendar.MILLISECOND] = 999
            return cal.time
        }

        /**
         * 获取日期当天的最后时间
         *
         *
         * 例如：今天日期是 2018-4-24 12:34:58，n=2，结果就是 2018-4-26 23:59:59:999
         *
         * @param date     日期
         * @param dayAfter 几天后
         * @return
         */
        fun getEndOfDay(date: Date?, dayAfter: Int): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DAY_OF_MONTH, dayAfter)
            cal[Calendar.HOUR_OF_DAY] = 23
            cal[Calendar.MINUTE] = 59
            cal[Calendar.SECOND] = 59
            cal[Calendar.MILLISECOND] = 999
            return cal.time
        }

        /**
         * 获取当前日期n天前的日期，返回String
         *
         * 例如：今天是2018-4-24，day=2， return 2018-4-22
         *
         * day=-2， return 2018-4-26
         *
         * @param day               【1：1天前， -1：1天后】
         * @param day
         * @param isNeedHMS:是否需要时分秒
         * @return
         */
        fun nDaysBeforeToday(day: Int, isNeedHMS: Boolean): String {
            return nDaysAfterToday(-day, isNeedHMS)
        }

        /**
         * 获取当前日期n天后的日期，返回String
         *
         * 例如：今天是2018-4-24，day=2， return 2018-4-26
         *
         * day=-2， return 2018-4-22
         *
         * @param day               【1：1天后，-1：1天前】
         * @param isNeedHMS:是否需要时分秒
         * @return
         */
        fun nDaysAfterToday(day: Int, isNeedHMS: Boolean): String {
            val rightNow = Calendar.getInstance()
            rightNow.timeInMillis = System.currentTimeMillis()
            rightNow.add(Calendar.DAY_OF_MONTH, day)
            return if (isNeedHMS) {
                date2String(
                    rightNow.time,
                    yyyyMMddHHmmss.get()
                )
            } else {
                date2String(
                    rightNow.time,
                    yyyyMMdd.get()
                )
            }
        }

        /**
         * 获取当前日期n天前的日期，返回String
         *
         * 例如：今天是2018-4-24，day=2， return 2018-4-22
         *
         * day=-2， return 2018-4-26
         *
         * @param day 【1：1天前， -1：1天后】
         * @param day
         * @return
         */
        fun nDaysBeforeToday(day: Int): Date {
            return nDaysAfterToday(-day)
        }

        /**
         * 获取当前日期n天后的日期，返回Date
         *
         * 例如：今天是2018-4-24，day=2， return 2018-4-26
         *
         * day=-2， return 2018-4-22
         *
         * @param day 【1：1天后，-1：1天前】
         * @return
         */
        fun nDaysAfterToday(day: Int): Date {
            val rightNow = Calendar.getInstance()
            rightNow.timeInMillis = System.currentTimeMillis()
            rightNow.add(Calendar.DAY_OF_MONTH, day)
            return rightNow.time
        }

        /**
         * 获取当前日期n月前的日期，返回Date
         *
         * 例如：今天是2018-04-24，month=2， return 2018-02-24
         *
         * month=-2， return 2018-06-24
         *
         * @param month 【1：1月前，-1：1月后】
         * @return 当前日期n月前的日期
         */
        fun nMonthsBeforeToday(month: Int): Date {
            return nMonthsAfterToday(-month)
        }

        /**
         * 获取当前日期n月后的日期，返回Date
         *
         * 例如：今天是2018-04-24，month=2， return 2018-06-24
         *
         * month=-2， return 2018-02-24
         *
         * @param month 【1：1月后，-1：1月前】
         * @return 当前日期n月后的日期
         */
        fun nMonthsAfterToday(month: Int): Date {
            val rightNow = Calendar.getInstance()
            rightNow.timeInMillis = System.currentTimeMillis()
            rightNow.add(Calendar.MONTH, month)
            return rightNow.time
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * time0 和 time1 格式都为 format
         *
         * @param time0  时间字符串 0
         * @param time1  时间字符串 1
         * @param format 时间格式
         * @param unit   单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpan(
            time0: String?,
            time1: String?,
            format: DateFormat?,
            @TimeConstants.Unit unit: Int
        ): Long {
            return millis2TimeSpan(
                Math.abs(string2Millis(time0, format) - string2Millis(time1, format)), unit
            )
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * @param date0 Date 类型时间 0
         * @param date1 Date 类型时间 1
         * @param unit  单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpan(
            date0: Date?,
            date1: Date?,
            @TimeConstants.Unit unit: Int
        ): Long {
            return millis2TimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), unit)
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * @param millis0 毫秒时间戳 0
         * @param millis1 毫秒时间戳 1
         * @param unit    单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpan(
            millis0: Long,
            millis1: Long,
            @TimeConstants.Unit unit: Int
        ): Long {
            return millis2TimeSpan(Math.abs(millis0 - millis1), unit)
        }

        /**
         * 获取合适型两个时间差
         *
         * time0 和 time1 格式都为 format
         *
         * @param time0     时间字符串 0
         * @param time1     时间字符串 1
         * @param format    时间格式
         * @param precision 精度
         *
         * precision = 0，返回 null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision = 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(
            time0: String?,
            time1: String?,
            format: DateFormat?,
            precision: Int
        ): String? {
            val delta = string2Millis(time0, format) - string2Millis(time1, format)
            return millis2FitTimeSpan(Math.abs(delta), precision)
        }

        /**
         * 获取合适型两个时间差
         *
         * @param date0     Date 类型时间 0
         * @param date1     Date 类型时间 1
         * @param precision 精度
         *
         * precision = 0，返回 null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision = 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(date0: Date?, date1: Date?, precision: Int): String? {
            return millis2FitTimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), precision)
        }

        /**
         * 获取合适型两个时间差
         *
         * @param millis0   毫秒时间戳 1
         * @param millis1   毫秒时间戳 2
         * @param precision 精度
         *
         * precision = 0，返回 null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision = 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(
            millis0: Long,
            millis1: Long,
            precision: Int
        ): String? {
            return millis2FitTimeSpan(Math.abs(millis0 - millis1), precision)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @param unit   单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpanByNow(
            time: String?,
            format: DateFormat?,
            @TimeConstants.Unit unit: Int
        ): Long {
            return getTimeSpan(getNowString(format), time, format, unit)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * @param date Date 类型时间
         * @param unit 单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpanByNow(date: Date?, @TimeConstants.Unit unit: Int): Long {
            return getTimeSpan(Date(), date, unit)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * @param millis 毫秒时间戳
         * @param unit   单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return unit 时间戳
         */
        fun getTimeSpanByNow(millis: Long, @TimeConstants.Unit unit: Int): Long {
            return getTimeSpan(System.currentTimeMillis(), millis, unit)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * time 格式为 format
         *
         * @param time      时间字符串
         * @param format    时间格式
         * @param precision 精度
         *
         *  * precision = 0，返回 null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision = 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(
            time: String?,
            format: DateFormat?,
            precision: Int
        ): String? {
            return getFitTimeSpan(getNowString(format), time, format, precision)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * @param date      Date 类型时间
         * @param precision 精度
         *
         *  * precision = 0，返回 null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision = 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(date: Date?, precision: Int): String? {
            return getFitTimeSpan(nowDate, date, precision)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * @param millis    毫秒时间戳
         * @param precision 精度
         *
         *  * precision = 0，返回 null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision = 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(millis: Long, precision: Int): String? {
            return getFitTimeSpan(System.currentTimeMillis(), millis, precision)
        }

        /**
         * 获取友好型与当前时间的差
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 友好型与当前时间的差
         *
         *  * 如果小于 1 秒钟内，显示刚刚
         *  * 如果在 1 分钟内，显示 XXX秒前
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        fun getFriendlyTimeSpanByNow(time: String?, format: DateFormat?): String {
            return getFriendlyTimeSpanByNow(string2Millis(time, format))
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param date Date 类型时间
         * @return 友好型与当前时间的差
         *
         *  * 如果小于 1 秒钟内，显示刚刚
         *  * 如果在 1 分钟内，显示 XXX秒前
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        fun getFriendlyTimeSpanByNow(date: Date): String {
            return getFriendlyTimeSpanByNow(date.time)
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param millis 毫秒时间戳
         * @return 友好型与当前时间的差
         *
         *  * 如果小于 1 秒钟内，显示刚刚
         *  * 如果在 1 分钟内，显示 XXX秒前
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        fun getFriendlyTimeSpanByNow(millis: Long): String {
            val now = System.currentTimeMillis()
            val span = now - millis
            if (span < 0) {
                return String.format("%tc", millis)
            }
            if (span < 1000) {
                return "刚刚"
            } else if (span < TimeConstants.MIN) {
                return String.format(Locale.getDefault(), "%d秒前", span / TimeConstants.SEC)
            } else if (span < TimeConstants.HOUR) {
                return String.format(Locale.getDefault(), "%d分钟前", span / TimeConstants.MIN)
            }
            // 获取当天 00:00
            val wee = weeOfToday
            return if (millis >= wee) {
                String.format("今天%tR", millis)
            } else if (millis >= wee - TimeConstants.DAY) {
                String.format("昨天%tR", millis)
            } else {
                String.format("%tF", millis)
            }
        }

        private val weeOfToday: Long
            private get() {
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0
                cal[Calendar.SECOND] = 0
                cal[Calendar.MINUTE] = 0
                cal[Calendar.MILLISECOND] = 0
                return cal.timeInMillis
            }

        /**
         * 年，单位【s】
         */
        private const val YEAR_S = 365 * 24 * 60 * 60

        /**
         * 月，单位【s】
         */
        private const val MONTH_S = 30 * 24 * 60 * 60

        /**
         * 天，单位【s】
         */
        private const val DAY_S = 24 * 60 * 60

        /**
         * 小时，单位【s】
         */
        private const val HOUR_S = 60 * 60

        /**
         * 分钟，单位【s】
         */
        private const val MINUTE_S = 60

        /**
         * 根据时间戳获取模糊型的时间描述。
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 模糊型的与当前时间的差
         *
         *  * 如果在 1 分钟内或者时间是未来的时间，显示刚刚
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 天内，显示XXX小时前
         *  * 如果在 1 月内，显示XXX天前
         *  * 如果在 1 年内，显示XXX月前
         *  * 如果在 1 年外，显示XXX年前
         *
         */
        fun getFuzzyTimeDescriptionByNow(time: String?, format: DateFormat?): String {
            return getFuzzyTimeDescriptionByNow(string2Millis(time, format))
        }

        /**
         * 根据时间戳获取模糊型的时间描述。
         *
         * @param date Date 类型时间
         * @return 模糊型的与当前时间的差
         *
         *  * 如果在 1 分钟内或者时间是未来的时间，显示刚刚
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 天内，显示XXX小时前
         *  * 如果在 1 月内，显示XXX天前
         *  * 如果在 1 年内，显示XXX月前
         *  * 如果在 1 年外，显示XXX年前
         *
         */
        fun getFuzzyTimeDescriptionByNow(date: Date): String {
            return getFuzzyTimeDescriptionByNow(date.time)
        }

        /**
         * 根据时间戳获取模糊型的时间描述。
         *
         * @param timestamp 时间戳 单位为毫秒
         * @return 模糊型的与当前时间的差
         *
         *  * 如果在 1 分钟内或者时间是未来的时间，显示刚刚
         *  * 如果在 1 小时内，显示 XXX分钟前
         *  * 如果在 1 天内，显示XXX小时前
         *  * 如果在 1 月内，显示XXX天前
         *  * 如果在 1 年内，显示XXX月前
         *  * 如果在 1 年外，显示XXX年前
         *
         */
        fun getFuzzyTimeDescriptionByNow(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            // 与现在时间相差秒数
            val timeGap = (currentTime - timestamp) / 1000
            val timeStr: String
            var span: Long
            if (Math.round(timeGap.toFloat() / YEAR_S).also { span = it.toLong() } > 0) {
                timeStr = span.toString() + "年前"
            } else if (Math.round(timeGap.toFloat() / MONTH_S).also { span = it.toLong() } > 0) {
                timeStr = span.toString() + "个月前"
            } else if (Math.round(timeGap.toFloat() / DAY_S)
                    .also { span = it.toLong() } > 0
            ) { // 1天以上
                timeStr = span.toString() + "天前"
            } else if (Math.round(timeGap.toFloat() / HOUR_S)
                    .also { span = it.toLong() } > 0
            ) { // 1小时-24小时
                timeStr = span.toString() + "小时前"
            } else if (Math.round(timeGap.toFloat() / MINUTE_S)
                    .also { span = it.toLong() } > 0
            ) { // 1分钟-59分钟
                timeStr = span.toString() + "分钟前"
            } else { // 1秒钟-59秒钟
                timeStr = "刚刚"
            }
            return timeStr
        }

        private fun timeSpan2Millis(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
            return timeSpan * unit
        }

        private fun millis2TimeSpan(millis: Long, @TimeConstants.Unit unit: Int): Long {
            return millis / unit
        }

        /**
         * @param millis    时间戳
         * @param precision 精度
         * @return
         */
        private fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
            var millis = millis
            var precision = precision
            if (millis < 0 || precision <= 0) {
                return null
            }
            precision = Math.min(precision, 5)
            val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
            if (millis == 0L) {
                return "0" + units[precision - 1]
            }
            val sb = StringBuilder()
            val unitLen = intArrayOf(
                TimeConstants.DAY,
                TimeConstants.HOUR,
                TimeConstants.MIN,
                TimeConstants.SEC,
                1
            )
            for (i in 0 until precision) {
                if (millis >= unitLen[i]) {
                    val mode = millis / unitLen[i]
                    millis -= mode * unitLen[i]
                    sb.append(mode).append(units[i])
                }
            }
            return sb.toString()
        }

        /**
         * 根据出生日期获取年龄
         *
         * @param birthDay 出生日期字符串
         * @param format   日期格式
         * @return 计算出的年龄
         */
        @Throws(IllegalArgumentException::class)
        fun getAgeByBirthDay(birthDay: String?, format: DateFormat?): Int {
            return getAgeByBirthDay(string2Date(birthDay, format))
        }

        /**
         * 根据出生日期获取年龄
         *
         * @param birthDay 出生日期
         * @return 计算出的年龄
         */
        @Throws(IllegalArgumentException::class)
        fun getAgeByBirthDay(birthDay: Date?): Int {
            val cal = Calendar.getInstance()
            require(!cal.before(birthDay)) { "The birthDay is before Now.It's unbelievable!" }
            val yearNow = cal[Calendar.YEAR]
            val monthNow = cal[Calendar.MONTH]
            val dayNow = cal[Calendar.DAY_OF_MONTH]
            cal.time = birthDay
            val yearBirth = cal[Calendar.YEAR]
            val monthBirth = cal[Calendar.MONTH]
            val dayBirth = cal[Calendar.DAY_OF_MONTH]
            var age = yearNow - yearBirth
            if (monthNow <= monthBirth) {
                if (monthNow == monthBirth) {
                    if (dayNow < dayBirth) {
                        age--
                    }
                } else {
                    age--
                }
            }
            return age
        }
        //==============================时间获取===============================//
        /**
         * 获取当前毫秒时间戳
         *
         * @return 毫秒时间戳
         */
        val nowMills: Long
            get() = System.currentTimeMillis()

        /**
         * 获取当前时间字符串
         *
         * 格式为 format
         *
         * @param format 时间格式
         * @return 时间字符串
         */
        fun getNowString(format: DateFormat?): String {
            return millis2String(System.currentTimeMillis(), format)
        }

        /**
         * 获取当前 Date
         *
         * @return Date 类型时间
         */
        val nowDate: Date
            get() = Date()

        /**
         * 判断是否今天
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(time: String?, format: DateFormat?): Boolean {
            return isToday(string2Millis(time, format))
        }

        /**
         * 判断是否今天
         *
         * @param date Date 类型时间
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(date: Date): Boolean {
            return isToday(date.time)
        }

        /**
         * 判断是否今天
         *
         * @param millis 毫秒时间戳
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(millis: Long): Boolean {
            val wee = weeOfToday
            return millis >= wee && millis < wee + TimeConstants.DAY
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的 Index 才是 1，周六为 7
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(time: String?, format: DateFormat?): Int {
            return getWeekIndex(string2Date(time, format))
        }

        /**
         * 得到年
         *
         * @param date Date对象
         * @return 年
         */
        fun getYear(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.YEAR]
        }

        /**
         * 得到月
         *
         * @param date Date对象
         * @return 月
         */
        fun getMonth(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.MONTH] + 1
        }

        /**
         * 得到日
         *
         * @param date Date对象
         * @return 日
         */
        fun getDay(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.DAY_OF_MONTH]
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的 Index 才是 1，周六为 7
         *
         * @param date Date 类型时间
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(date: Date?): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.DAY_OF_WEEK]
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的 Index 才是 1，周六为 7
         *
         * @param millis 毫秒时间戳
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(millis: Long): Int {
            return getWeekIndex(millis2Date(millis))
        }

        //=====================获取生肖、星座=========================//
        private val CHINESE_ZODIAC =
            arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")

        /**
         * 获取生肖
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 生肖
         */
        fun getChineseZodiac(time: String?, format: DateFormat?): String {
            return getChineseZodiac(string2Date(time, format))
        }

        /**
         * 获取生肖
         *
         * @param date Date 类型时间
         * @return 生肖
         */
        fun getChineseZodiac(date: Date?): String {
            val cal = Calendar.getInstance()
            cal.time = date
            return CHINESE_ZODIAC[cal[Calendar.YEAR] % 12]
        }

        /**
         * 获取生肖
         *
         * @param millis 毫秒时间戳
         * @return 生肖
         */
        fun getChineseZodiac(millis: Long): String {
            return getChineseZodiac(millis2Date(millis))
        }

        /**
         * 获取生肖
         *
         * @param year 年
         * @return 生肖
         */
        fun getChineseZodiac(year: Int): String {
            return CHINESE_ZODIAC[year % 12]
        }

        private val ZODIAC_FLAGS = intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)
        private val ZODIAC = arrayOf(
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"
        )

        /**
         * 获取星座
         *
         * time 格式为 format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 生肖
         */
        fun getZodiac(time: String?, format: DateFormat?): String {
            return getZodiac(string2Date(time, format))
        }

        /**
         * 获取星座
         *
         * @param date Date 类型时间
         * @return 星座
         */
        fun getZodiac(date: Date?): String {
            val cal = Calendar.getInstance()
            cal.time = date
            val month = cal[Calendar.MONTH] + 1
            val day = cal[Calendar.DAY_OF_MONTH]
            return getZodiac(month, day)
        }

        /**
         * 获取星座
         *
         * @param millis 毫秒时间戳
         * @return 星座
         */
        fun getZodiac(millis: Long): String {
            return getZodiac(millis2Date(millis))
        }

        /**
         * 获取星座
         *
         * @param month 月
         * @param day   日
         * @return 星座
         */
        fun getZodiac(month: Int, day: Int): String {
            return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1]) month - 1 else (month + 10) % 12]
        }
    }
    //============================时间类型转化================================//
    /**
     * Don't let anyone instantiate this class.
     */
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}