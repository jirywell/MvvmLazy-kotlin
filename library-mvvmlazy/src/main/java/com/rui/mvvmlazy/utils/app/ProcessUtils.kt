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
package com.rui.mvvmlazy.utils.app

import android.Manifest.permission
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.common.KLog
import java.util.*

/**
 * <pre>
 * desc   : 进程相关工具类(6.0以后失效）
 * author : zjr
 * time   : 2018/4/28 上午12:35
</pre> *
 */
class ProcessUtils private constructor() {
    companion object {
        /**
         * 获取前台线程包名
         *
         * 当不是查看当前 App，且 SDK 大于 21 时，
         * 需添加权限
         * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
         *
         * @return 前台应用包名
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @RequiresPermission(permission.PACKAGE_USAGE_STATS)
        fun getForegroundProcessName(): String? {
            val manager = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return null
            val pInfo = manager.runningAppProcesses
            if (pInfo != null && pInfo.size > 0) {
                for (aInfo in pInfo) {
                    if (aInfo.importance
                        == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                        return aInfo.processName
                    }
                }
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                val packageManager = appContext.packageManager
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                val list =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                KLog.i("ProcessUtils", list.toString())
                if (list.size <= 0) {
                    KLog.i(
                        "ProcessUtils",
                        "getForegroundProcessName() called" + ": 无\"有权查看使用权限的应用\"选项"
                    )
                    return null
                }
                try { // 有"有权查看使用权限的应用"选项
                    val info =
                        packageManager.getApplicationInfo(appContext.packageName, 0)
                    val aom = appContext
                        .getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    if (aom != null) {
                        if (aom.checkOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                            ) != AppOpsManager.MODE_ALLOWED
                        ) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            appContext.startActivity(intent)
                        }
                        if (aom.checkOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                            ) != AppOpsManager.MODE_ALLOWED
                        ) {
                            KLog.i("ProcessUtils", "没有打开\"有权查看使用权限的应用\"选项")
                            return null
                        }
                    }
                    val usageStatsManager = appContext
                        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    var usageStatsList: List<UsageStats>? = null
                    if (usageStatsManager != null) {
                        val endTime = System.currentTimeMillis()
                        val beginTime = endTime - 86400000 * 7
                        usageStatsList = usageStatsManager
                            .queryUsageStats(
                                UsageStatsManager.INTERVAL_BEST,
                                beginTime, endTime
                            )
                    }
                    if (usageStatsList == null || usageStatsList.isEmpty()) {
                        return null
                    }
                    var recentStats: UsageStats? = null
                    for (usageStats in usageStatsList) {
                        if (recentStats == null
                            || usageStats.lastTimeUsed > recentStats.lastTimeUsed
                        ) {
                            recentStats = usageStats
                        }
                    }
                    return recentStats?.packageName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        /**
         * 获取后台服务进程
         *
         * @return 后台服务进程
         */
        fun getAllBackgroundProcesses(): Set<String> {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.runningAppProcesses
            val set: HashSet<String> = HashSet()
            if (info != null) {
                for (aInfo in info) {
                    Collections.addAll(set, *aInfo.pkgList)
                }
            }
            return set
        }

        /**
         * 判断进程是否在运行
         *
         * @return 后台服务进程
         */
        fun isProcessRunning(processName: String): Boolean {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return false
            val info = am.runningAppProcesses
            if (info != null) {
                for (aInfo in info) {
                    if (processName == aInfo.processName) {
                        return true
                    }
                }
            }
            return false
        }

        /**
         * 杀死所有的后台服务进程
         *
         * 需添加权限
         * `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @return 被暂时杀死的服务集合
         */
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun killAllBackgroundProcesses(): Set<String> {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return emptySet()
            var info = am.runningAppProcesses
            val set: MutableSet<String> = HashSet()
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    am.killBackgroundProcesses(pkg)
                    set.add(pkg)
                }
            }
            info = am.runningAppProcesses
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    set.remove(pkg)
                }
            }
            return set
        }

        /**
         * 杀死后台服务进程
         *
         * 需添加权限
         * `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @param packageName 包名
         * @return `true`: 杀死成功<br></br>`false`: 杀死失败
         */
        @JvmStatic
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun killBackgroundProcesses(packageName: String): Boolean {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return false
            var info = am.runningAppProcesses
            if (info == null || info.size == 0) {
                return true
            }
            for (aInfo in info) {
                if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                    KLog.d("[killBackgroundProcesses]：" + aInfo.processName)
                    am.killBackgroundProcesses(packageName)
                }
            }
            info = am.runningAppProcesses
            if (info == null || info.size == 0) {
                return true
            }
            for (aInfo in info) {
                if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                    return false
                }
            }
            return true
        }

        /**
         * 清理后台进程与服务
         *
         * @return 被清理的数量
         */
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun gc(): Int {
            val beforeGCDeviceUsableMemory = getDeviceUsableMemory().toLong()
            var count = 0 // 清理掉的进程数
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            // 获取正在运行的service列表
            val serviceList = am.getRunningServices(100)
            if (serviceList != null) {
                for (service in serviceList) {
                    if (service.pid == Process.myPid()) {
                        continue
                    }
                    try {
                        Process.killProcess(service.pid)
                        count++
                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
            }

            // 获取正在运行的进程列表
            val processList = am.runningAppProcesses
            if (processList != null) {
                for (process in processList) {
                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                    if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        // pkgList 得到该进程下运行的包名
                        val pkgList = process.pkgList
                        for (pkgName in pkgList) {
                            KLog.d("======正在杀死包名：$pkgName")
                            try {
                                am.killBackgroundProcesses(pkgName)
                                count++
                            } catch (e: Exception) { // 防止意外发生
                                e.stackTrace
                            }
                        }
                    }
                }
            }
            KLog.d("清理了" + (getDeviceUsableMemory() - beforeGCDeviceUsableMemory) + "M内存")
            return count
        }

        /**
         * 获取设备的可用内存大小（单位：M)
         *
         * @return 当前内存大小
         */
        fun getDeviceUsableMemory(): Int {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            // 返回当前系统的可用内存
            return (mi.availMem / (1024 * 1024)).toInt()
        }

        /**
         * 判断应用当前所处进程是否是主进程
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isMainProcess(): Boolean {
            return appContext.packageName == getCurrentProcessName()
        }

        /**
         * 获取当前进程的名字
         *
         * @return 当前进程的名字
         */
        fun getCurrentProcessName(): String? {
            val am = appContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return null
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) {
                return null
            }
            val pid = Process.myPid()
            for (aInfo in info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName
                    }
                }
            }
            return ""
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}