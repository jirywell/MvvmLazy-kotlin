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
package com.rui.mvvmlazy.utils.file

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.app.PathUtils
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getUriByFile
import com.rui.mvvmlazy.utils.app.PathUtils.Companion.getUriForFile
import okhttp3.internal.and
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * <pre>
 * desc   : 文件相关工具类
 * author : zjr
 * time   : 2018/4/28 上午12:53
</pre> *
 */
class FileUtils private constructor() {
    interface OnReplaceListener {
        fun onReplace(): Boolean
    }

    companion object {
        private val LINE_SEP = System.getProperty("line.separator")
        //================文件路径获取===================//
        /**
         * SD卡是否存在
         *
         * @return
         */
        val isSDCardExist: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()

        /**
         * 获取磁盘的缓存目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/cache;<br></br>
         * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/cache;
         */
        val diskCacheDir: String
            get() = if (isSDCardExist && appContext.externalCacheDir != null) appContext.externalCacheDir!!
                .path else appContext.cacheDir.path

        /**
         * 获取磁盘的自定义缓存目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/cache/fileDir;<br></br>
         * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/cache/fileDir;
         */
        fun getDiskCacheDir(fileDir: String): String {
            return diskCacheDir + File.separator + fileDir
        }

        /**
         * 获取磁盘的文件目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/files;<br></br>
         * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files;
         */
        val diskFilesDir: String
            get() {
                val file = appContext.getExternalFilesDir(null)
                return if (isSDCardExist && file != null) file.path else appContext.filesDir.path
            }

        /**
         * 获取磁盘的自定义文件目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/files/fileDir;<br></br>
         * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files/fileDir;
         */
        fun getDiskFilesDir(fileDir: String): String {
            return diskFilesDir + File.separator + fileDir
        }

        /**
         * 获取磁盘目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/fileDir;<br></br>
         * 存在: /storage/emulated/0/fileDir; 或者 /storage/sdcard/fileDir
         */
        fun getDiskDir(fileDir: String): String {
            return if (isSDCardExist) {
                Environment.getExternalStorageDirectory().toString() + File.separator + fileDir
            } else {
                appContext.getDir(fileDir, Context.MODE_PRIVATE).path
            }
        }

        /**
         * 获取磁盘目录
         *
         * @return SD卡不存在: /data/data/com.xxx.xxx/com.xxx.xxx;<br></br>
         * 存在: /storage/emulated/0/com.xxx.xxx; 或者 /storage/sdcard/com.xxx.xxx
         */
        val diskDir: String
            get() = getDiskDir(appContext.packageName)
        //=================判断文件是否存在========================//
        /**
         * 获取文件的路径
         *
         * @param dirPath  目录
         * @param fileName 文件名
         * @return 拼接的文件的路径
         */
        fun getFilePath(dirPath: String, fileName: String): String {
            return getDirPath(dirPath) + fileName
        }

        /**
         * 获取文件目录的路径，自动补齐"/"
         *
         * @param dirPath 目录路径
         * @return 自动补齐"/"的目录路径
         */
        fun getDirPath(dirPath: String): String {
            var dirPath = dirPath
            if (isSpace(dirPath)) {
                return ""
            }
            if (!dirPath.trim { it <= ' ' }.endsWith(File.separator)) {
                dirPath = dirPath.trim { it <= ' ' } + File.separator
            }
            return dirPath
        }

        /**
         * 根据文件路径获取文件
         *
         * @param filePath 文件路径
         * @return 文件
         */
        fun getFileByPath(filePath: String?): File? {
            return if (isSpace(filePath)) null else File(filePath)
        }

        /**
         * 判断文件是否存在
         *
         * @param file 文件
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(file: File?): Boolean {
            if (file == null) {
                return false
            }
            return if (file.exists()) {
                true
            } else isFileExists(file.absolutePath)
        }

        /**
         * 判断文件是否存在
         *
         * @param filePath 文件路径
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(filePath: String?): Boolean {
            val file = getFileByPath(filePath)
                ?: return false
            return if (file.exists()) {
                true
            } else isFileExistsApi29(filePath)
        }

        /**
         * Android 10判断文件是否存在的方法
         *
         * @param filePath 文件路径
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        private fun isFileExistsApi29(filePath: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                var afd: AssetFileDescriptor? = null
                try {
                    val uri = Uri.parse(filePath)
                    afd = appContext.contentResolver.openAssetFileDescriptor(uri, "r")
                    if (afd == null) {
                        return false
                    } else {
                        CloseUtils.Companion.closeIOQuietly(afd)
                    }
                } catch (e: FileNotFoundException) {
                    return false
                } finally {
                    CloseUtils.Companion.closeIOQuietly(afd)
                }
                return true
            }
            return false
        }

        /**
         * 判断文件目录是否存在
         *
         * @param dirPath 文件目录路径
         * @return
         */
        fun isFolderExist(dirPath: String?): Boolean {
            return isFolderExist(getFileByPath(dirPath))
        }

        /**
         * 判断文件目录是否存在
         *
         * @param dir
         * @return
         */
        fun isFolderExist(dir: File?): Boolean {
            return dir != null && dir.exists() && dir.isDirectory
        }
        //======================文件判断=======================//
        /**
         * 判断是否是目录
         *
         * @param dirPath 目录路径
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isDir(dirPath: String?): Boolean {
            return isDir(getFileByPath(dirPath))
        }

        /**
         * 判断是否是目录
         *
         * @param file 文件
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isDir(file: File?): Boolean {
            return file != null && file.exists() && file.isDirectory
        }

        /**
         * 判断是否是文件
         *
         * @param filePath 文件路径
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(filePath: String?): Boolean {
            return isFile(getFileByPath(filePath))
        }

        /**
         * 判断是否是文件
         *
         * @param file 文件
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(file: File?): Boolean {
            return file != null && file.exists() && file.isFile
        }
        //=======================重命名文件=======================//
        /**
         * 重命名文件
         *
         * @param filePath 文件路径
         * @param newName  新名称
         * @return `true`: 重命名成功<br></br>`false`: 重命名失败
         */
        fun rename(filePath: String?, newName: String): Boolean {
            return rename(getFileByPath(filePath), newName)
        }

        /**
         * 重命名文件
         *
         * @param file    文件
         * @param newName 新名称
         * @return `true`: 重命名成功<br></br>`false`: 重命名失败
         */
        fun rename(file: File?, newName: String): Boolean {
            // 文件为空返回 false
            if (file == null) {
                return false
            }
            // 文件不存在返回 false
            if (!file.exists()) {
                return false
            }
            // 新的文件名为空返回 false
            if (isSpace(newName)) {
                return false
            }
            // 如果文件名没有改变返回 true
            if (newName == file.name) {
                return true
            }
            val newFile = File(file.parent + File.separator + newName)
            // 如果重命名的文件已存在返回 false
            return (!newFile.exists()
                    && file.renameTo(newFile))
        }
        //=======================创建文件=======================//
        /**
         * 文件不存在就创建，存在则返回文件，不存在则创建并返回文件
         *
         * @param filePath
         * @return 文件
         */
        fun isFileNotExistCreate(filePath: String?): File? {
            val file = getFileByPath(filePath) ?: return null
            // 如果存在，是文件则返回
            if (file.exists()) {
                return file
            }
            //目录没有创建成功，直接返回null
            return if (!createOrExistsDir(file.parentFile)) {
                null
            } else try {
                file.createNewFile()
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param dirPath 目录路径
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(dirPath: String?): Boolean {
            return createOrExistsDir(getFileByPath(dirPath))
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(file: File?): Boolean {
            // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功
         *
         * @param filePath 文件路径
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(filePath: String?): Boolean {
            return createOrExistsFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(file: File?): Boolean {
            if (file == null) {
                return false
            }
            // 如果存在，是文件则返回 true，是目录则返回 false
            if (file.exists()) {
                return file.isFile
            }
            return if (!createOrExistsDir(file.parentFile)) {
                false
            } else try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 判断文件是否存在，存在则在创建之前删除
         *
         * @param filePath 文件路径
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(filePath: String?): Boolean {
            return createFileByDeleteOldFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，存在则在创建之前删除
         *
         * @param file 文件
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(file: File?): Boolean {
            if (file == null) {
                return false
            }
            // 文件存在并且删除失败返回 false
            if (file.exists() && !file.delete()) {
                return false
            }
            // 创建目录失败返回 false
            return if (!createOrExistsDir(file.parentFile)) {
                false
            } else try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
        //=======================文件复制或移动=======================//
        /**
         * 复制或移动目录
         *
         * @param srcDirPath  源目录路径
         * @param destDirPath 目标目录路径
         * @param listener    是否覆盖监听器
         * @param isMove      是否移动
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveDir(
            srcDirPath: String,
            destDirPath: String,
            listener: OnReplaceListener,
            isMove: Boolean
        ): Boolean {
            return copyOrMoveDir(
                getFileByPath(srcDirPath),
                getFileByPath(destDirPath),
                listener,
                isMove
            )
        }

        /**
         * 复制或移动目录
         *
         * @param srcDir   源目录
         * @param destDir  目标目录
         * @param listener 是否覆盖监听器
         * @param isMove   是否移动
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveDir(
            srcDir: File?,
            destDir: File?,
            listener: OnReplaceListener?,
            isMove: Boolean
        ): Boolean {
            if (srcDir == null || destDir == null) {
                return false
            }
            // 如果目标目录在源目录中则返回 false，看不懂的话好好想想递归怎么结束
            // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
            // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
            // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
            val srcPath = srcDir.path + File.separator
            val destPath = destDir.path + File.separator
            if (destPath.contains(srcPath)) {
                return false
            }
            // 源文件不存在或者不是目录则返回 false
            if (!srcDir.exists() || !srcDir.isDirectory) {
                return false
            }
            if (destDir.exists()) {
                if (listener == null || listener.onReplace()) { // 需要覆盖则删除旧目录
                    if (!deleteAllInDir(destDir)) { // 删除文件失败的话返回 false
                        return false
                    }
                } else { // 不需要覆盖直接返回即可 true
                    return true
                }
            }
            // 目标目录不存在返回 false
            if (!createOrExistsDir(destDir)) {
                return false
            }
            val files = srcDir.listFiles()
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    // 如果操作失败返回 false
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                        return false
                    }
                } else if (file.isDirectory) {
                    // 如果操作失败返回 false
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                        return false
                    }
                }
            }
            return !isMove || deleteDir(srcDir)
        }

        /**
         * 复制或移动文件
         *
         * @param srcFilePath  源文件路径
         * @param destFilePath 目标文件路径
         * @param listener     是否覆盖监听器
         * @param isMove       是否移动
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveFile(
            srcFilePath: String,
            destFilePath: String,
            listener: OnReplaceListener,
            isMove: Boolean
        ): Boolean {
            return copyOrMoveFile(
                getFileByPath(srcFilePath),
                getFileByPath(destFilePath),
                listener,
                isMove
            )
        }

        /**
         * 复制或移动文件
         *
         * @param srcFile  源文件
         * @param destFile 目标文件
         * @param listener 是否覆盖监听器
         * @param isMove   是否移动
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveFile(
            srcFile: File?,
            destFile: File?,
            listener: OnReplaceListener?,
            isMove: Boolean
        ): Boolean {
            if (srcFile == null || destFile == null) {
                return false
            }
            // 如果源文件和目标文件相同则返回 false
            if (srcFile == destFile) {
                return false
            }
            // 源文件不存在或者不是文件则返回 false
            if (!srcFile.exists() || !srcFile.isFile) {
                return false
            }
            if (destFile.exists()) { // 目标文件存在
                if (listener == null || listener.onReplace()) { // 需要覆盖则删除旧文件
                    if (!destFile.delete()) { // 删除文件失败的话返回 false
                        return false
                    }
                } else { // 不需要覆盖直接返回即可 true
                    return true
                }
            }
            // 目标目录不存在返回 false
            return if (!createOrExistsDir(destFile.parentFile)) {
                false
            } else try {
                (FileIOUtils.Companion.writeFileFromIS(destFile, FileInputStream(srcFile), false)
                        && !(isMove && !deleteFile(srcFile)))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            }
        }
        //=======================文件复制=======================//
        /**
         * 复制目录
         *
         * @param srcDirPath  源目录路径
         * @param destDirPath 目标目录路径
         * @param listener    是否覆盖监听器
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyDir(
            srcDirPath: String?,
            destDirPath: String?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
        }

        /**
         * 复制目录
         *
         * @param srcDir   源目录
         * @param destDir  目标目录
         * @param listener 是否覆盖监听器
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyDir(
            srcDir: File?,
            destDir: File?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyOrMoveDir(srcDir, destDir, listener, false)
        }

        /**
         * 复制文件
         *
         * @param srcFilePath  源文件路径
         * @param destFilePath 目标文件路径
         * @param listener     是否覆盖监听器
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyFile(
            srcFilePath: String?,
            destFilePath: String?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
        }

        /**
         * 复制文件
         *
         * @param srcFile  源文件
         * @param destFile 目标文件
         * @param listener 是否覆盖监听器
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyFile(
            srcFile: File?,
            destFile: File?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyOrMoveFile(srcFile, destFile, listener, false)
        }
        //=======================文件移动=======================//
        /**
         * 移动目录
         *
         * @param srcDirPath  源目录路径
         * @param destDirPath 目标目录路径
         * @param listener    是否覆盖监听器
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveDir(
            srcDirPath: String?,
            destDirPath: String?,
            listener: OnReplaceListener?
        ): Boolean {
            return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
        }

        /**
         * 移动目录
         *
         * @param srcDir   源目录
         * @param destDir  目标目录
         * @param listener 是否覆盖监听器
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveDir(
            srcDir: File?,
            destDir: File?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyOrMoveDir(srcDir, destDir, listener, true)
        }

        /**
         * 移动文件
         *
         * @param srcFilePath  源文件路径
         * @param destFilePath 目标文件路径
         * @param listener     是否覆盖监听器
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveFile(
            srcFilePath: String?,
            destFilePath: String?,
            listener: OnReplaceListener?
        ): Boolean {
            return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
        }

        /**
         * 移动文件
         *
         * @param srcFile  源文件
         * @param destFile 目标文件
         * @param listener 是否覆盖监听器
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveFile(
            srcFile: File?,
            destFile: File?,
            listener: OnReplaceListener?
        ): Boolean {
            return copyOrMoveFile(srcFile, destFile, listener, true)
        }
        //=======================文件删除=======================//
        /**
         * 删除文件或目录
         *
         * @param filePath 文件的路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun delete(filePath: String?): Boolean {
            return delete(getFileByPath(filePath))
        }

        /**
         * 删除文件或目录
         *
         * @param file 文件
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun delete(file: File?): Boolean {
            if (file == null) {
                return false
            }
            return if (file.isDirectory) {
                deleteDir(file)
            } else deleteFile(file)
        }

        /**
         * 删除目录
         *
         * @param dirPath 目录路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dirPath: String?): Boolean {
            return deleteDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录
         *
         * @param dir 目录
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dir: File?): Boolean {
            if (dir == null) {
                return false
            }
            // 目录不存在返回 true
            if (!dir.exists()) {
                return true
            }
            // 不是目录返回 false
            if (!dir.isDirectory) {
                return false
            }
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.isFile) {
                        if (!file.delete()) {
                            return false
                        }
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) {
                            return false
                        }
                    }
                }
            }
            return dir.delete()
        }

        /**
         * 删除文件
         *
         * @param srcFilePath 文件路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(srcFilePath: String?): Boolean {
            return deleteFile(getFileByPath(srcFilePath))
        }

        /**
         * 删除文件
         *
         * @param file 文件
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(file: File?): Boolean {
            return file != null && (!file.exists() || file.isFile && file.delete())
        }

        /**
         * 安全删除文件.
         *
         * @param file 需要删除的文件
         * @return 是否删除成功
         */
        fun deleteFileSafely(file: File?): Boolean {
            if (file != null && file.exists()) {
                val tmpPath = file.parent + File.separator + System.currentTimeMillis()
                val tmp = File(tmpPath)
                file.renameTo(tmp)
                return tmp.delete()
            }
            return false
        }

        /**
         * 删除目录下所有东西
         *
         * @param dirPath 目录路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteAllInDir(dirPath: String?): Boolean {
            return deleteAllInDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录下所有东西
         *
         * @param dir 目录
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteAllInDir(dir: File?): Boolean {
            return deleteFilesInDirWithFilter(dir) { true }
        }

        /**
         * 删除目录下所有文件
         *
         * @param dirPath 目录路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDir(dirPath: String?): Boolean {
            return deleteFilesInDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录下所有文件
         *
         * @param dir 目录
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDir(dir: File?): Boolean {
            return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
        }

        /**
         * 删除目录下所有过滤的文件【目录不删除】
         *
         * @param dirPath 目录路径
         * @param filter  过滤器
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDirWithFilter(
            dirPath: String?,
            filter: FileFilter
        ): Boolean {
            return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
        }

        /**
         * 删除目录下所有过滤的文件【目录不删除】
         *
         * @param dir    目录
         * @param filter 过滤器
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter): Boolean {
            if (dir == null) {
                return false
            }
            // 目录不存在返回 true
            if (!dir.exists()) {
                return true
            }
            // 不是目录返回 false
            if (!dir.isDirectory) {
                return false
            }
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (filter.accept(file)) {
                        if (file.isFile) {
                            if (!file.delete()) {
                                return false
                            }
                        } else if (file.isDirectory) {
                            if (!deleteDir(file)) {
                                return false
                            }
                        }
                    }
                }
            }
            return true
        }
        /**
         * 获取目录下所有文件
         *
         * @param dirPath     目录路径
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        //=======================文件遍历、过滤、获取=======================//
        /**
         * 获取目录下所有文件
         *
         * 不递归进子目录
         *
         * @param dirPath 目录路径
         * @return 文件链表
         */
        @JvmOverloads
        fun listFilesInDir(dirPath: String?, isRecursive: Boolean = false): List<File>? {
            return listFilesInDir(getFileByPath(dirPath), isRecursive)
        }
        /**
         * 获取目录下所有文件
         *
         * @param dir         目录
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        /**
         * 获取目录下所有文件
         *
         * 不递归进子目录
         *
         * @param dir 目录
         * @return 文件链表
         */
        @JvmOverloads
        fun listFilesInDir(dir: File?, isRecursive: Boolean = false): List<File>? {
            return listFilesInDirWithFilter(dir, { true }, isRecursive)
        }

        /**
         * 获取目录下所有过滤的文件
         *
         * 不递归进子目录
         *
         * @param dirPath 目录路径
         * @param filter  过滤器
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(
            dirPath: String?,
            filter: FileFilter
        ): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false)
        }

        /**
         * 获取目录下所有过滤的文件
         *
         * @param dirPath     目录路径
         * @param filter      过滤器
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(
            dirPath: String?,
            filter: FileFilter,
            isRecursive: Boolean
        ): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
        }
        /**
         * 获取目录下所有过滤的文件
         *
         * @param dir         目录
         * @param filter      过滤器
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        /**
         * 获取目录下所有过滤的文件
         *
         * 不递归进子目录
         *
         * @param dir    目录
         * @param filter 过滤器
         * @return 文件链表
         */
        @JvmOverloads
        fun listFilesInDirWithFilter(
            dir: File?,
            filter: FileFilter,
            isRecursive: Boolean = false
        ): List<File>? {
            if (!isDir(dir)) {
                return null
            }
            val list: MutableList<File> = ArrayList()
            val files = dir!!.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (filter.accept(file)) {
                        list.add(file)
                    }
                    if (isRecursive && file.isDirectory) {
                        list.addAll(listFilesInDirWithFilter(file, filter, true)!!)
                    }
                }
            }
            return list
        }
        //=======================文件信息（修改时间、大小、编码格式等）=======================//
        /**
         * 获取文件最后修改的毫秒时间戳
         *
         * @param filePath 文件路径
         * @return 文件最后修改的毫秒时间戳
         */
        fun getFileLastModified(filePath: String?): Long {
            return getFileLastModified(getFileByPath(filePath))
        }

        /**
         * 获取文件最后修改的毫秒时间戳
         *
         * @param file 文件
         * @return 文件最后修改的毫秒时间戳
         */
        fun getFileLastModified(file: File?): Long {
            return file?.lastModified() ?: -1
        }

        /**
         * 简单获取文件编码格式
         *
         * @param filePath 文件路径
         * @return 文件编码
         */
        fun getFileCharsetSimple(filePath: String?): String {
            return getFileCharsetSimple(getFileByPath(filePath))
        }

        /**
         * 简单获取文件编码格式
         *
         * @param file 文件
         * @return 文件编码
         */
        fun getFileCharsetSimple(file: File?): String {
            var p = 0
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file))
                p = (`is`.read() shl 8) + `is`.read()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.Companion.closeIO(`is`)
            }
            return when (p) {
                0xefbb -> "UTF-8"
                0xfffe -> "Unicode"
                0xfeff -> "UTF-16BE"
                else -> "GBK"
            }
        }

        /**
         * 获取文件行数
         *
         * @param filePath 文件路径
         * @return 文件行数
         */
        fun getFileLines(filePath: String?): Int {
            return getFileLines(getFileByPath(filePath))
        }

        /**
         * 获取文件行数
         *
         * 比 readLine 要快很多
         *
         * @param file 文件
         * @return 文件行数
         */
        fun getFileLines(file: File?): Int {
            var count = 1
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file))
                val buffer = ByteArray(1024)
                var readChars: Int
                if (LINE_SEP.endsWith("\n")) {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i].equals('\n')) {
                                ++count
                            }
                        }
                    }
                } else {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i].equals('\r')) {
                                ++count
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.Companion.closeIO(`is`)
            }
            return count
        }

        /**
         * 获取目录大小
         *
         * @param dirPath 目录路径
         * @return 文件大小
         */
        fun getDirSize(dirPath: String?): String {
            return getDirSize(getFileByPath(dirPath))
        }

        /**
         * 获取目录大小
         *
         * @param dir 目录
         * @return 文件大小
         */
        fun getDirSize(dir: File?): String {
            val len = getDirLength(dir)
            return if (len == -1L) "" else byte2FitMemorySize(len)
        }

        /**
         * 获取文件大小
         *
         * @param filePath 文件路径
         * @return 文件大小
         */
        fun getFileSize(filePath: String): String {
            val len = getFileLength(filePath)
            return if (len == -1L) "" else byte2FitMemorySize(len)
        }

        /**
         * 获取文件大小
         *
         * @param file 文件
         * @return 文件大小
         */
        fun getFileSize(file: File?): String {
            val len = getFileLength(file)
            return if (len == -1L) "" else byte2FitMemorySize(len)
        }

        /**
         * 获取目录长度
         *
         * @param dirPath 目录路径
         * @return 目录长度
         */
        fun getDirLength(dirPath: String?): Long {
            return getDirLength(getFileByPath(dirPath))
        }

        /**
         * 获取目录长度
         *
         * @param dir 目录
         * @return 目录长度
         */
        fun getDirLength(dir: File?): Long {
            if (!isDir(dir)) {
                return -1
            }
            var len: Long = 0
            val files = dir!!.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    len += if (file.isDirectory) {
                        getDirLength(file)
                    } else {
                        file.length()
                    }
                }
            }
            return len
        }

        /**
         * 获取文件长度
         *
         * @param filePath 文件路径
         * @return 文件长度
         */
        fun getFileLength(filePath: String): Long {
            val isURL = filePath.matches(Regex("[a-zA-z]+://[^\\s]*"))
            if (isURL) {
                try {
                    val conn = URL(filePath).openConnection() as HttpURLConnection
                    conn.setRequestProperty("Accept-Encoding", "identity")
                    conn.connect()
                    return if (conn.responseCode == 200) {
                        conn.contentLength.toLong()
                    } else -1
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return getFileLength(getFileByPath(filePath))
        }

        /**
         * 获取文件长度
         *
         * @param file 文件
         * @return 文件长度
         */
        fun getFileLength(file: File?): Long {
            return if (!isFile(file)) {
                -1
            } else file!!.length()
        }

        /**
         * 获取文件的 MD5 校验码
         *
         * @param filePath 文件路径
         * @return 文件的 MD5 校验码
         */
        fun getFileMD5ToString(filePath: String?): String? {
            val file = if (isSpace(filePath)) null else File(filePath)
            return getFileMD5ToString(file)
        }

        /**
         * 获取文件的 MD5 校验码
         *
         * @param file 文件
         * @return 文件的 MD5 校验码
         */
        fun getFileMD5ToString(file: File?): String? {
            return bytes2HexString(getFileMD5(file))
        }

        /**
         * 获取文件的 MD5 校验码
         *
         * @param filePath 文件路径
         * @return 文件的 MD5 校验码
         */
        fun getFileMD5(filePath: String?): ByteArray? {
            return getFileMD5(getFileByPath(filePath))
        }

        /**
         * 获取文件的 MD5 校验码
         *
         * @param file 文件
         * @return 文件的 MD5 校验码
         */
        fun getFileMD5(file: File?): ByteArray? {
            if (file == null) {
                return null
            }
            var fis: InputStream? = null
            try {
                fis = getFileInputStream(file)
                val digest = MessageDigest.getInstance("MD5")
                val buffer = ByteArray(8192)
                var len: Int
                while (fis!!.read(buffer).also { len = it } != -1) {
                    digest.update(buffer, 0, len)
                }
                return digest.digest()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.Companion.closeIO(fis)
            }
            return null
        }

        /**
         * 获取文件输入流
         *
         * @param file 文件
         * @return
         * @throws FileNotFoundException
         */
        @Throws(IOException::class)
        fun getFileInputStream(file: File?): InputStream? {
            return if (isScopedStorageMode && PathUtils.isPublicPath(
                    file
                )
            ) {
                val uri = getUriByFile(file)
                appContext.contentResolver.openInputStream(uri!!)
            } else {
                FileInputStream(file)
            }
        }

        /**
         * 是否是分区存储模式：在公共目录下file的api无效了
         *
         * @return 是否是分区存储模式
         */
        val isScopedStorageMode: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()

        /**
         * 获取全路径中的最长目录
         *
         * @param file 文件
         * @return filePath 最长目录
         */
        fun getDirName(file: File?): String? {
            return if (file == null) {
                null
            } else getDirName(
                file.path
            )
        }

        /**
         * 获取全路径中的最长目录
         *
         * @param filePath 文件路径
         * @return filePath 最长目录
         */
        fun getDirName(filePath: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
        }

        /**
         * 获取全路径中的文件名
         *
         * @param file 文件
         * @return 文件名
         */
        fun getFileName(file: File?): String? {
            return if (file == null) {
                null
            } else getFileName(file.path)
        }

        /**
         * 获取全路径中的文件名
         *
         * @param filePath 文件路径
         * @return 文件名
         */
        fun getFileName(filePath: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
        }

        /**
         *
         * 获取全路径中的不带拓展名的文件名(不带路径)
         *
         * 例如:aa/bb/cc.png  --> cc
         *
         * @param file 文件
         * @return 不带拓展名的文件名
         */
        fun getFileNameNoExtension(file: File?): String? {
            return if (file == null) {
                null
            } else getFileNameNoExtension(file.path)
        }

        /**
         *
         * 获取全路径中的不带拓展名的文件名(不带路径)
         *
         * 例如:aa/bb/cc.png  --> cc
         *
         * @param filePath 文件路径
         * @return 不带拓展名的文件名
         */
        fun getFileNameNoExtension(filePath: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            if (lastSep == -1) {
                return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
            }
            return if (lastPoi == -1 || lastSep > lastPoi) {
                filePath.substring(lastSep + 1)
            } else filePath.substring(lastSep + 1, lastPoi)
        }

        /**
         * 获取全路径中的不带拓展名的文件名(带路径）
         *
         *
         *
         * 例如:aa/bb/cc.png  --> aa/bb/cc
         *
         * @param filePath 文件路径
         * @return 不带拓展名的文件名(带路径 ）
         */
        fun getFileNameNoExtensionWithPath(filePath: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastPoi = filePath.lastIndexOf('.')
            return if (lastPoi == -1) {
                filePath
            } else filePath.substring(0, lastPoi)
        }

        /**
         * 改变文件的拓展名
         *
         *
         *
         * 例如:aa/bb/cc.png  --> aa/bb/cc.xxx
         *
         * @param filePath      文件路径
         * @param extensionName 拓展名 [.xxx]
         * @return 改变拓展名的文件路径
         */
        fun changeFileExtension(filePath: String, extensionName: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastPoi == -1 || lastSep >= lastPoi) {
                ""
            } else filePath.substring(0, lastPoi) + extensionName
        }

        /**
         * 获取全路径中的文件拓展名
         *
         * @param file 文件
         * @return 文件拓展名
         */
        fun getFileExtension(file: File?): String? {
            return if (file == null) {
                null
            } else getFileExtension(file.path)
        }

        /**
         * 获取全路径中的文件拓展名
         *
         * @param filePath 文件路径
         * @return 文件拓展名
         */
        fun getFileExtension(filePath: String): String {
            if (isSpace(filePath)) {
                return filePath
            }
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastPoi == -1 || lastSep >= lastPoi) {
                ""
            } else filePath.substring(lastPoi + 1)
        }

        ///////////////////////////////////////////////////////////////////////////
        // copy from ConvertUtils
        ///////////////////////////////////////////////////////////////////////////
        private val hexDigits = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )

        /**
         * byteArr 转 hexString
         *
         * 例如：
         * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
         *
         * @param bytes 字节数组
         * @return 16 进制大写字符串
         */
        private fun bytes2HexString(bytes: ByteArray?): String? {
            if (bytes == null) {
                return null
            }
            val len = bytes.size
            if (len <= 0) {
                return null
            }
            val ret = CharArray(len shl 1)
            var i = 0
            var j = 0
            while (i < len) {
                ret[j++] = hexDigits[bytes[i].toInt() ushr 4 and 0x0f]
                ret[j++] = hexDigits[bytes[i] and 0x0f]
                i++
            }
            return String(ret)
        }

        /**
         * 字节数转合适内存大小
         *
         * 保留 3 位小数
         *
         * @param byteNum 字节数
         * @return 合适内存大小
         */
        @SuppressLint("DefaultLocale")
        fun byte2FitMemorySize(byteNum: Long): String {
            return if (byteNum < 0) {
                "shouldn't be less than zero!"
            } else if (byteNum < 1024) {
                String.format("%.3fB", byteNum.toDouble())
            } else if (byteNum < 1048576) {
                String.format("%.3fKB", byteNum.toDouble() / 1024)
            } else if (byteNum < 1073741824) {
                String.format("%.3fMB", byteNum.toDouble() / 1048576)
            } else {
                String.format("%.3fGB", byteNum.toDouble() / 1073741824)
            }
        }

        /**
         * 字节数转合适内存大小
         *
         * 保留 N 位小数
         *
         * @param byteNum 字节数
         * @param length  小数位数
         * @return
         */
        @SuppressLint("DefaultLocale")
        fun byte2FitMemorySize(byteNum: Long, length: Int): String {
            return if (byteNum < 0) {
                "shouldn't be less than zero!"
            } else if (byteNum < 1024) {
                String.format("%." + length + "fB", byteNum.toDouble())
            } else if (byteNum < 1048576) {
                String.format("%." + length + "fKB", byteNum.toDouble() / 1024)
            } else if (byteNum < 1073741824) {
                String.format("%." + length + "fMB", byteNum.toDouble() / 1048576)
            } else {
                String.format("%." + length + "fGB", byteNum.toDouble() / 1073741824)
            }
        }

        /**
         * 通知系统去扫描文件
         *
         * @param filePath The path of file.
         */
        fun notifySystemToScan(filePath: String?) {
            notifySystemToScan(getFileByPath(filePath))
        }

        /**
         * 通知系统去扫描文件.
         *
         * @param file The file.
         */
        fun notifySystemToScan(file: File?) {
            if (file == null || !file.exists()) {
                return
            }
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = getUriForFile(file)
            intent.data = uri
            appContext.sendBroadcast(intent)
        }

        /**
         * 获取手机文件大小的总和
         *
         * @param anyPathInFs 任意文件路径.
         * @return 手机文件大小的总和
         */
        fun getFsTotalSize(anyPathInFs: String?): Long {
            if (TextUtils.isEmpty(anyPathInFs)) {
                return 0
            }
            val statFs = StatFs(anyPathInFs)
            val blockSize: Long
            val totalSize: Long
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.blockSizeLong
                totalSize = statFs.blockCountLong
            } else {
                blockSize = statFs.blockSize.toLong()
                totalSize = statFs.blockCount.toLong()
            }
            return blockSize * totalSize
        }

        /**
         * 获取手机文件可用大小
         *
         * @param anyPathInFs 任意文件路径
         * @return 手机文件可用大小
         */
        fun getFsAvailableSize(anyPathInFs: String?): Long {
            if (TextUtils.isEmpty(anyPathInFs)) {
                return 0
            }
            val statFs = StatFs(anyPathInFs)
            val blockSize: Long
            val availableSize: Long
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.blockSizeLong
                availableSize = statFs.availableBlocksLong
            } else {
                blockSize = statFs.blockSize.toLong()
                availableSize = statFs.availableBlocks.toLong()
            }
            return blockSize * availableSize
        }

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
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}