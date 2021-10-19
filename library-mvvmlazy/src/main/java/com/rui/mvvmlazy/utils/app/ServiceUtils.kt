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
package com.rui.mvvmlazy.utils.app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.common.KLog
import java.util.*

/**
 * <pre>
 * desc   : 服务相关工具类
 * author : zjr
 * time   : 2018/4/28 上午12:35
</pre> *
 */
class ServiceUtils() {
    companion object {
        /**
         * 获取所有运行的服务
         *
         * @return 服务名集合
         */
        fun getAllRunningService(): Set<*>? {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return emptySet<Any>()
            val info = am.getRunningServices(Int.MAX_VALUE)
            val names: MutableSet<String> = HashSet()
            if (info == null || info.size == 0) {
                return null
            }
            for (aInfo in info) {
                names.add(aInfo.service.className)
            }
            return names
        }

        /**
         * 启动服务
         *
         * @param className 完整包名的服务类名
         */
        fun startService(className: String?) {
            try {
                startService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 启动服务
         *
         * @param cls 服务类
         */
        fun startService(cls: Class<*>?) {
            val intent = Intent(appContext, cls)
            appContext.startService(intent)
        }

        /**
         * 停止服务
         *
         * @param className 完整包名的服务类名
         * @return `true`: 停止成功<br></br>`false`: 停止失败
         */
        fun stopService(className: String?): Boolean {
            return try {
                stopService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 停止服务
         *
         * @param cls 服务类
         * @return `true`: 停止成功<br></br>`false`: 停止失败
         */
        fun stopService(cls: Class<*>?): Boolean {
            val intent = Intent(appContext, cls)
            return appContext.stopService(intent)
        }

        /**
         * 绑定服务
         *
         * @param className 完整包名的服务类名
         * @param conn      服务连接对象
         * @param flags     绑定选项
         *
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            className: String?,
            conn: ServiceConnection?,
            flags: Int
        ) {
            try {
                bindService(Class.forName(className), conn, flags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 绑定服务
         *
         * @param cls   服务类
         * @param conn  服务连接对象
         * @param flags 绑定选项
         *
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            cls: Class<*>?,
            conn: ServiceConnection?,
            flags: Int
        ) {
            val intent = Intent(appContext, cls)
            appContext.bindService(intent, conn!!, flags)
        }

        /**
         * 解绑服务
         *
         * @param conn 服务连接对象
         */
        fun unbindService(conn: ServiceConnection?) {
            appContext.unbindService(conn!!)
        }

        /**
         * 判断服务是否运行
         *
         * @param className 完整包名的服务类名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isServiceRunning(className: String): Boolean {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return false
            val info = am.getRunningServices(Int.MAX_VALUE)
            if (info == null || info.size == 0) {
                return false
            }
            for (aInfo in info) {
                if (className == aInfo.service.className) {
                    return true
                }
            }
            return false
        }

        /**
         * 停止指定应用的所以运行的服务
         *
         * @param context
         */
        @JvmStatic
        fun stopAllRunningService(context: Context) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val servicesList = activityManager.getRunningServices(Int.MAX_VALUE)
            for (si in servicesList) {
                if (context.packageName == si.service.packageName) {
                    KLog.d("[stopService]:" + si.service.className)
                    stopService(si.service.className)
                }
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}