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
 * 存储相关常量
 */
object MemoryConstants {
    /**
     * Byte与Byte的倍数
     */
    const val BYTE = 1

    /**
     * KB与Byte的倍数
     */
    const val KB = 1024

    /**
     * MB与Byte的倍数
     */
    const val MB = 1048576

    /**
     * GB与Byte的倍数
     */
    const val GB = 1073741824

    @IntDef(BYTE, KB, MB, GB)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Unit
}