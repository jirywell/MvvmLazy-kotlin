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

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.rui.mvvmlazy.utils.common.KLog
import com.rui.mvvmlazy.utils.common.ShellUtils
import java.io.File
import java.io.IOException

/**
 * PackageUtils
 *
 * **Install package**
 *  * [PackageUtils.installNormal]
 *  * [PackageUtils.installAppSilent]
 *  * [PackageUtils.install]
 *
 *
 * **Uninstall package**
 *  * [PackageUtils.uninstallNormal]
 *  * [PackageUtils.uninstallSilent]
 *  * [PackageUtils.uninstall]
 *
 *
 * **Is system application**
 *  * [PackageUtils.isSystemApplication]
 *  * [PackageUtils.isSystemApplication]
 *  * [PackageUtils.isSystemApplication]
 *
 *
 * **Others**
 *  * [PackageUtils.getInstallLocation] get system install location
 * package's name is packageName is on the top of the stack
 *  * [PackageUtils.startInstalledAppDetails] start
 * InstalledAppDetails Activity
 *
 * <pre>
 * desc   :
 * author : zjr
 * time   : 2018/4/28 上午12:32
</pre> *
 */
class PackageUtils() {
    companion object {
        /**
         * App installation location settings values
         */
        private const val APP_INSTALL_AUTO = 0
        private const val APP_INSTALL_INTERNAL = 1
        private const val APP_INSTALL_EXTERNAL = 2

        /**
         * apk安装的请求码
         */
        const val REQUEST_CODE_INSTALL_APP = 999

        /**
         * apk安装
         *
         * @param context
         * @param apkFile apk文件
         * @return
         */
        @Throws(IOException::class)
        fun install(context: Context, apkFile: File): Boolean {
            return install(context, apkFile.canonicalPath)
        }

        /**
         * apk安装
         *
         * @param context
         * @param filePath apk文件的路径
         * @return
         */
        fun install(context: Context, filePath: String): Boolean {
            return if (isSystemApplication(context)
                || ShellUtils.checkRootPermission()
            ) {
                installAppSilent(context, filePath)
            } else installNormal(context, filePath)
        }

        /**
         * 静默安装 App
         *
         * 非 root 需添加权限
         * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
         *
         * @param filePath 文件路径
         * @return `true`: 安装成功<br></br>`false`: 安装失败
         */
        @RequiresPermission(permission.INSTALL_PACKAGES)
        fun installAppSilent(context: Context, filePath: String): Boolean {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                installAppSilentBelow24(context, filePath)
            } else {
                installAppSilentAbove24(
                    context.packageName,
                    filePath
                )
            }
        }

        /**
         * 静默安装 App 在Android7.0以下起作用
         *
         * 非 root 需添加权限
         * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
         *
         * @param filePath 文件路径
         * @return `true`: 安装成功<br></br>`false`: 安装失败
         */
        @RequiresPermission(permission.INSTALL_PACKAGES)
        private fun installAppSilentBelow24(context: Context, filePath: String): Boolean {
            val file = getFileByPath(filePath)
            if (!isFileExists(file)) {
                return false
            }
            val pmParams = " -r " + getInstallLocationParams()
            val command = StringBuilder()
                .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                .append(pmParams).append(" ")
                .append(filePath.replace(" ", "\\ "))
            val commandResult = ShellUtils.execCommand(
                command.toString(), !isSystemApplication(context), true
            )
            return (commandResult.successMsg != null
                    && (commandResult.successMsg!!.contains("Success") || commandResult.successMsg!!
                .contains("success")))
        }
        //===============================//
        /**
         * 静默安装 App 在Android7.0及以上起作用
         *
         * 非 root 需添加权限
         * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
         *
         * @param filePath 文件路径
         * @return `true`: 安装成功<br></br>`false`: 安装失败
         */
        @RequiresPermission(permission.INSTALL_PACKAGES)
        private fun installAppSilentAbove24(packageName: String, filePath: String): Boolean {
            val file = getFileByPath(filePath)
            if (!isFileExists(file)) {
                return false
            }
            val isRoot = isDeviceRooted
            val command = "pm install -i $packageName --user 0 $filePath"
            val commandResult = ShellUtils.execCommand(command, isRoot)
            return (commandResult.successMsg != null
                    && commandResult.successMsg!!.lowercase().contains("success"))
        }

        /**
         * install package normal by system intent
         *
         * @param context
         * @param filePath file path of package
         * @return whether apk exist
         */
        private fun installNormal(context: Context, filePath: String): Boolean {
            val file = getFileByPath(filePath)
            return isFileExists(file) && installNormal(context, file)
        }

        /**
         * 使用系统的意图进行apk安装
         *
         * @param context
         * @param appFile
         * @return
         */
        private fun installNormal(context: Context, appFile: File?): Boolean {
            try {
                val intent = getInstallAppIntent(context, appFile)
                if (context.packageManager.queryIntentActivities(intent!!, 0).size > 0) {
                    if (context is Activity) {
                        context.startActivityForResult(intent, REQUEST_CODE_INSTALL_APP)
                    } else {
                        context.startActivity(intent)
                    }
                    return true
                }
            } catch (e: Exception) {
                KLog.e("使用系统的意图进行apk安装失败！", e)
            }
            return false
        }

        /**
         * 获取安装apk的意图
         *
         * @param context
         * @param appFile
         * @return
         */
        fun getInstallAppIntent(context: Context, appFile: File?): Intent? {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                    intent.flags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".updateFileProvider",
                        appFile!!
                    )
                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
                } else {
                    intent.setDataAndType(
                        Uri.fromFile(appFile),
                        "application/vnd.android.package-archive"
                    )
                }
                return intent
            } catch (e: Exception) {
                KLog.e("获取安装的意图失败！", e)
            }
            return null
        }

        /**
         * 根据文件路径获取文件
         *
         * @param filePath 文件路径
         * @return 文件
         */
        private fun getFileByPath(filePath: String): File? {
            return if (isSpace(filePath)) null else File(filePath)
        }

        /**
         * 判断字符串是否为 null 或全为空白字符
         *
         * @param s 待校验字符串
         * @return `true`: null 或全空白字符<br></br> `false`: 不为 null 且不全空白字符
         */
        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        /**
         * 判断文件是否存在
         *
         * @param file 文件
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        private fun isFileExists(file: File?): Boolean {
            return file != null && file.exists()
        }

        /**
         * 判断设备是否 root
         *
         * @return the boolean`true`: 是<br></br>`false`: 否
         */
        private val isDeviceRooted: Boolean
            private get() {
                val su = "su"
                val locations = arrayOf(
                    "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                    "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"
                )
                for (location in locations) {
                    if (File(location + su).exists()) {
                        return true
                    }
                }
                return false
            }

        /**
         * uninstall according conditions
         *
         *  * if system application or rooted, see
         * [.uninstallSilent]
         *  * else see [.uninstallNormal]
         *
         *
         * @param context
         * @param packageName package name of app
         * @return
         */
        fun uninstall(context: Context, packageName: String?): Int {
            if (isSystemApplication(context) || ShellUtils.checkRootPermission()) {
                return uninstallSilent(context, packageName)
            }
            return if (uninstallNormal(
                    context,
                    packageName
                )
            ) DELETE_SUCCEEDED else DELETE_FAILED_INVALID_PACKAGE
        }

        /**
         * uninstall package normal by system intent
         *
         * @param context
         * @param packageName package name of app
         * @return whether package name is empty
         */
        fun uninstallNormal(context: Context, packageName: String?): Boolean {
            if (packageName == null || packageName.length == 0) {
                return false
            }
            val i = Intent(
                Intent.ACTION_DELETE,
                Uri.parse(StringBuilder(32).append("package:").append(packageName).toString())
            )
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
            return true
        }
        /**
         * uninstall package silent by root
         *
         * **Attentions:**
         *  * Don't call this on the ui thread, it may costs some times.
         *  * You should add **android.permission.DELETE_PACKAGES** in
         * manifest, so no need to request root permission, if you are system app.
         *
         *
         * @param context     file path of package
         * @param packageName package name of app
         * @param isKeepData  whether keep the data and cache directories around after
         * package removal
         * @return
         *  * [.DELETE_SUCCEEDED] means uninstall success
         *  * [.DELETE_FAILED_INTERNAL_ERROR] means internal error
         *  * [.DELETE_FAILED_INVALID_PACKAGE] means package name
         * error
         *  * [.DELETE_FAILED_PERMISSION_DENIED] means permission
         * denied
         */
        /**
         * uninstall package and clear data of app silent by root
         *
         * @param context
         * @param packageName package name of app
         * @return
         * @see .uninstallSilent
         */
        @JvmOverloads
        fun uninstallSilent(
            context: Context?,
            packageName: String?,
            isKeepData: Boolean = true
        ): Int {
            if (packageName == null || packageName.length == 0) {
                return DELETE_FAILED_INVALID_PACKAGE
            }
            /**
             * if context is system app, don't need root permission, but should add
             * <uses-permission android:name="android.permission.DELETE_PACKAGES"></uses-permission>
             * in mainfest
             */
            val command =
                StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                    .append(if (isKeepData) " -k " else " ").append(packageName.replace(" ", "\\ "))
            val commandResult =
                ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true)
            if (commandResult.successMsg != null && (commandResult.successMsg!!.contains("Success") || commandResult.successMsg!!.contains(
                    "success"
                ))
            ) {
                return DELETE_SUCCEEDED
            }
            KLog.e(
                StringBuilder().append("uninstallSilent successMsg:")
                    .append(commandResult.successMsg).append(", ErrorMsg:")
                    .append(commandResult.errorMsg).toString()
            )
            if (commandResult.errorMsg == null) {
                return DELETE_FAILED_INTERNAL_ERROR
            }
            return if (commandResult.errorMsg!!.contains("Permission denied")) {
                DELETE_FAILED_PERMISSION_DENIED
            } else DELETE_FAILED_INTERNAL_ERROR
        }

        /**
         * whether context is system application
         *
         * @param context
         * @return
         */
        fun isSystemApplication(context: Context?): Boolean {
            return context != null && isSystemApplication(context, context.packageName)
        }

        /**
         * whether packageName is system application
         *
         * @param context
         * @param packageName
         * @return
         */
        fun isSystemApplication(context: Context?, packageName: String?): Boolean {
            return context != null && isSystemApplication(context.packageManager, packageName)
        }

        /**
         * whether packageName is system application
         *
         * @param packageManager
         * @param packageName
         * @return
         *  * if packageManager is null, return false
         *  * if package name is null or is empty, return false
         *  * if package name not exit, return false
         *  * if package name exit, but not system app, return false
         *  * else return true
         *
         */
        fun isSystemApplication(packageManager: PackageManager?, packageName: String?): Boolean {
            if (packageManager == null || packageName == null || packageName.length == 0) {
                return false
            }
            try {
                val app = packageManager.getApplicationInfo(packageName, 0)
                return app != null && app.flags and ApplicationInfo.FLAG_SYSTEM > 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * get app version code
         *
         * @param context
         * @return
         */
        fun getAppVersionCode(context: Context?): Int {
            if (context != null) {
                val pm = context.packageManager
                if (pm != null) {
                    val pi: PackageInfo?
                    try {
                        pi = pm.getPackageInfo(context.packageName, 0)
                        if (pi != null) {
                            return pi.versionCode
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
            return -1
        }



        /**
         * get system install location<br></br>
         * can be set by System Menu Setting->Storage->Prefered install location
         *
         * @return
         */
        private fun getInstallLocation(): Int {
            val commandResult = ShellUtils.execCommand(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location",
                false,
                true
            )
            if (commandResult.result == 0 && commandResult.successMsg != null && commandResult.successMsg!!.isNotEmpty()) {
                try {
                    val location = commandResult.successMsg!!.substring(0, 1).toInt()
                    when (location) {
                        APP_INSTALL_INTERNAL -> return APP_INSTALL_INTERNAL
                        APP_INSTALL_EXTERNAL -> return APP_INSTALL_EXTERNAL
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    KLog.e("pm get-install-location error")
                }
            }
            return APP_INSTALL_AUTO
        }

        /**
         * get params for pm install location
         *
         * @return
         */
        private fun getInstallLocationParams(): String {
            val location = getInstallLocation()
            when (location) {
                APP_INSTALL_INTERNAL -> return "-f"
                APP_INSTALL_EXTERNAL -> return "-s"
            }
            return ""
        }

        /**
         * start InstalledAppDetails Activity
         *
         * @param context
         * @param packageName
         * @since android.provider.Settings#ACTION_APPLICATION_DETAILS_SETTINGS
         * requires API level 9 (current min is 8):
         */
        @SuppressLint("InlinedApi")
        fun startInstalledAppDetails(context: Context, packageName: String?) {
            val intent = Intent()
            val sdkVersion = Build.VERSION.SDK_INT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
            } else {
                intent.action = Intent.ACTION_VIEW
                intent.setClassName(
                    "com.android.settings",
                    "com.android.settings.InstalledAppDetails"
                )
                intent.putExtra(
                    if (sdkVersion == Build.VERSION_CODES.FROYO) "pkg" else "com.android.settings.ApplicationPkgName",
                    packageName
                )
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        /**
         * 检查某个应用是否安装
         *
         * @param context
         * @param packageName 包名
         * @return
         */
        fun checkAPP(context: Context, packageName: String?): Boolean {
            return if (packageName == null || "" == packageName) {
                false
            } else try {
                val info = context.packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES
                )
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        /**
         * 使用系统安装apk
         *
         * @param filePath
         */
        fun installApk(context: Context, filePath: String?) {
            val apkFile = File(filePath)
            if (!apkFile.exists()) {
                return
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.setDataAndType(
                Uri.parse("file://$apkFile"),
                "application/vnd.android.package-archive"
            )
            context.startActivity(i)
        }

        /**
         * 打开指定的应用
         *
         * @param context
         * @param appInfo
         */
        fun openApp(context: Context, appInfo: ApplicationInfo) {
            openApp(context, appInfo.packageName)
        }

        /**
         * 打开指定的应用
         *
         * @param context
         * @param packageName
         */
        fun openApp(context: Context, packageName: String): Boolean {
            val intent = getAppOpenIntentByPackageName(context, packageName, true)
            if (intent != null) {
                context.startActivity(intent)
                return true
            }
            return false
        }

        /**
         * 切换app，如果app已打开就直接切换回去，不重新打开
         * @param context
         * @param packageName
         * @return
         */
        fun switchApp(context: Context, packageName: String): Boolean {
            val pkgContext = getPackageContext(context, packageName)
            val intent = getAppOpenIntentByPackageName(context, packageName, false)
            if (pkgContext != null && intent != null) {
                pkgContext.startActivity(intent)
                return true
            }
            return false
        }

        private fun getAppOpenIntentByPackageName(
            context: Context,
            packageName: String,
            isReopen: Boolean
        ): Intent? {
            // MainActivity完整名
            var mainAct: String? = null
            // 根据包名寻找MainActivity
            val pkgMag = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            if (!isReopen) { //不重新打开
                intent.flags =
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            @SuppressLint("WrongConstant") val list =
                pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
            for (i in list.indices) {
                val info = list[i]
                if (info.activityInfo.packageName == packageName) {
                    mainAct = info.activityInfo.name
                    break
                }
            }
            if (TextUtils.isEmpty(mainAct)) {
                return null
            }
            intent.component = ComponentName(packageName, mainAct!!)
            return intent
        }

        private fun getPackageContext(context: Context, packageName: String): Context? {
            var pkgContext: Context? = null
            if (context.packageName == packageName) {
                pkgContext = context
            } else {
                // 创建第三方应用的上下文环境
                try {
                    pkgContext = context.createPackageContext(
                        packageName, Context.CONTEXT_IGNORE_SECURITY
                                or Context.CONTEXT_INCLUDE_CODE
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            return pkgContext
        }

        /**
         * Uninstall return code<br></br>
         * uninstall success.
         */
        const val DELETE_SUCCEEDED = 1

        /**
         * Uninstall return code<br></br>
         * uninstall fail if the system failed to delete the package for an
         * unspecified reason.
         */
        const val DELETE_FAILED_INTERNAL_ERROR = -1

        /**
         * Uninstall return code<br></br>
         * uninstall fail if the system failed to delete the package because it is
         * the active DevicePolicy manager.
         */
        const val DELETE_FAILED_DEVICE_POLICY_MANAGER = -2

        /**
         * Uninstall return code<br></br>
         * uninstall fail if pcakge name is invalid
         */
        const val DELETE_FAILED_INVALID_PACKAGE = -3

        /**
         * Uninstall return code<br></br>
         * uninstall fail if permission denied
         */
        const val DELETE_FAILED_PERMISSION_DENIED = -4
    }

    /**
     * Don't let anyone instantiate this class.
     */
    init {
        throw Error("Do not need instantiate!")
    }
}