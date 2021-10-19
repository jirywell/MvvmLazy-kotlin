package com.rui.mvvmlazy.utils.constant

import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtStoragePath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtDownloadsPath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtPicturesPath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtDCIMPath
import androidx.annotation.IntDef
import com.rui.mvvmlazy.utils.constant.MemoryConstants
import com.rui.mvvmlazy.utils.constant.PathConstants
import android.annotation.SuppressLint
import android.Manifest.permission
import androidx.annotation.StringDef
import com.rui.mvvmlazy.utils.constant.TimeConstants
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by zjr on 2020/5/14.
 * 时间相关常量
 */
object TimeConstants {
    /**
     * 毫秒与毫秒的倍数
     */
    const val MSEC = 1

    /**
     * 秒与毫秒的倍数
     */
    const val SEC = 1000

    /**
     * 分与毫秒的倍数
     */
    const val MIN = 60000

    /**
     * 时与毫秒的倍数
     */
    const val HOUR = 3600000

    /**
     * 天与毫秒的倍数
     */
    const val DAY = 86400000

    @IntDef(MSEC, SEC, MIN, HOUR, DAY)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Unit
}