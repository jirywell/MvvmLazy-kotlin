/*
 * Copyright (C) 2020 jirui_zhao(jirui_zhao@163.com)
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
 *
 */
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
import java.io.File

/**
 * 文件路径常量
 *
 * @author zjr
 * @since 2020/6/6 1:51 AM
 */
object PathConstants {
    val EXT_STORAGE_PATH = getExtStoragePath()
    val EXT_STORAGE_DIR = EXT_STORAGE_PATH + File.separator
    val APP_EXT_STORAGE_PATH = EXT_STORAGE_DIR + "Android"
    val EXT_DOWNLOADS_PATH = getExtDownloadsPath()
    val EXT_PICTURES_PATH = getExtPicturesPath()
    val EXT_DCIM_PATH = getExtDCIMPath()
}