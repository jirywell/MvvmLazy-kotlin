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
package com.rui.mvvmlazy.utils.constant

import android.Manifest
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtStoragePath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtDownloadsPath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtPicturesPath
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getExtDCIMPath
import androidx.annotation.IntDef
import com.rui.mvvmlazy.utils.constant.MemoryConstants
import com.rui.mvvmlazy.utils.constant.PathConstants
import android.annotation.SuppressLint
import android.Manifest.permission
import android.os.Build
import androidx.annotation.StringDef
import com.rui.mvvmlazy.utils.constant.TimeConstants
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 权限相关常量
 *
 * @author zjr
 * @date 2018/2/15 下午1:45
 */
@SuppressLint("InlinedApi")
object PermissionConstants {
    const val REQUEST_OVERLAY_PERMISSION_CODE = 10001
    const val REQUEST_WRITE_SETTINGS_PERMISSION_CODE = 10002
    const val CALENDAR = Manifest.permission_group.CALENDAR
    const val CAMERA = Manifest.permission_group.CAMERA
    const val CONTACTS = Manifest.permission_group.CONTACTS
    const val LOCATION = Manifest.permission_group.LOCATION
    const val MICROPHONE = Manifest.permission_group.MICROPHONE
    const val PHONE = Manifest.permission_group.PHONE
    const val SENSORS = Manifest.permission_group.SENSORS
    const val SMS = Manifest.permission_group.SMS
    const val STORAGE = Manifest.permission_group.STORAGE
    val ALL_PERMISSION = arrayOf(
        CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE
    )
    private val GROUP_CALENDAR = arrayOf(
        permission.READ_CALENDAR, permission.WRITE_CALENDAR
    )
    private val GROUP_CAMERA = arrayOf(
        permission.CAMERA
    )
    private val GROUP_CONTACTS = arrayOf(
        permission.READ_CONTACTS, permission.WRITE_CONTACTS, permission.GET_ACCOUNTS
    )
    private val GROUP_LOCATION = arrayOf(
        permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
    )
    private val GROUP_MICROPHONE = arrayOf(
        permission.RECORD_AUDIO
    )
    private val GROUP_PHONE = arrayOf(
        permission.READ_PHONE_STATE, permission.READ_PHONE_NUMBERS, permission.CALL_PHONE,
        permission.READ_CALL_LOG, permission.WRITE_CALL_LOG, permission.ADD_VOICEMAIL,
        permission.USE_SIP, permission.PROCESS_OUTGOING_CALLS, permission.ANSWER_PHONE_CALLS
    )
    private val GROUP_PHONE_BELOW_O = arrayOf(
        permission.READ_PHONE_STATE, permission.READ_PHONE_NUMBERS, permission.CALL_PHONE,
        permission.READ_CALL_LOG, permission.WRITE_CALL_LOG, permission.ADD_VOICEMAIL,
        permission.USE_SIP, permission.PROCESS_OUTGOING_CALLS
    )
    private val GROUP_SENSORS = arrayOf(
        permission.BODY_SENSORS
    )
    private val GROUP_SMS = arrayOf(
        permission.SEND_SMS, permission.RECEIVE_SMS, permission.READ_SMS,
        permission.RECEIVE_WAP_PUSH, permission.RECEIVE_MMS
    )
    private val GROUP_STORAGE = arrayOf(
        permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE
    )

    fun getPermissions(@Permission permission: String): Array<String> {
        when (permission) {
            CALENDAR -> return GROUP_CALENDAR
            CAMERA -> return GROUP_CAMERA
            CONTACTS -> return GROUP_CONTACTS
            LOCATION -> return GROUP_LOCATION
            MICROPHONE -> return GROUP_MICROPHONE
            PHONE -> return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                GROUP_PHONE_BELOW_O
            } else {
                GROUP_PHONE
            }
            SENSORS -> return GROUP_SENSORS
            SMS -> return GROUP_SMS
            STORAGE -> return GROUP_STORAGE
            else -> {
            }
        }
        return arrayOf(permission)
    }

    @StringDef(CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE)
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class Permission
}