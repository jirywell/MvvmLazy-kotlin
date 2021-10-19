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

import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * <pre>
 * desc   : 压缩相关工具类
 * author : zjr
 * time   : 2018/4/28 上午12:53
</pre> *
 */
class ZipUtils() {
    companion object {
        private const val BUFFER_LEN = 8192

        /**
         * 批量压缩文件
         *
         * @param resFiles    待压缩文件路径集合
         * @param zipFilePath 压缩文件路径
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO错误时抛出
         */
        @Throws(IOException::class)
        fun zipFiles(
            resFiles: Collection<String?>?,
            zipFilePath: String?
        ): Boolean {
            return zipFiles(resFiles, zipFilePath, null)
        }

        /**
         * 批量压缩文件
         *
         * @param resFilePaths 待压缩文件路径集合
         * @param zipFilePath  压缩文件路径
         * @param comment      压缩文件的注释
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO错误时抛出
         */
        @Throws(IOException::class)
        fun zipFiles(
            resFilePaths: Collection<String?>?,
            zipFilePath: String?,
            comment: String?
        ): Boolean {
            if (resFilePaths == null || zipFilePath == null) {
                return false
            }
            var zos: ZipOutputStream? = null
            return try {
                zos = ZipOutputStream(FileOutputStream(zipFilePath))
                for (resFile in resFilePaths) {
                    if (!zipFile(getFileByPath(resFile), "", zos, comment)) {
                        return false
                    }
                }
                true
            } finally {
                if (zos != null) {
                    zos.finish()
                    CloseUtils.Companion.closeIO(zos)
                }
            }
        }
        /**
         * 批量压缩文件
         *
         * @param resFiles 待压缩文件集合
         * @param zipFile  压缩文件
         * @param comment  压缩文件的注释
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO错误时抛出
         */
        /**
         * 批量压缩文件
         *
         * @param resFiles 待压缩文件集合
         * @param zipFile  压缩文件
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO错误时抛出
         */
        @JvmOverloads
        @Throws(IOException::class)
        fun zipFiles(
            resFiles: Collection<File?>?,
            zipFile: File?,
            comment: String? = null
        ): Boolean {
            if (resFiles == null || zipFile == null) {
                return false
            }
            var zos: ZipOutputStream? = null
            return try {
                zos = ZipOutputStream(FileOutputStream(zipFile))
                for (resFile in resFiles) {
                    if (!zipFile(resFile, "", zos, comment)) {
                        return false
                    }
                }
                true
            } finally {
                if (zos != null) {
                    zos.finish()
                    CloseUtils.Companion.closeIO(zos)
                }
            }
        }

        /**
         * 压缩文件
         *
         * @param resFilePath 待压缩文件路径
         * @param zipFilePath 压缩文件路径
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun zipFile(
            resFilePath: String?,
            zipFilePath: String?
        ): Boolean {
            return zipFile(getFileByPath(resFilePath), getFileByPath(zipFilePath), null)
        }

        /**
         * 压缩文件
         *
         * @param resFilePath 待压缩文件路径
         * @param zipFilePath 压缩文件路径
         * @param comment     压缩文件的注释
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun zipFile(
            resFilePath: String?,
            zipFilePath: String?,
            comment: String?
        ): Boolean {
            return zipFile(getFileByPath(resFilePath), getFileByPath(zipFilePath), comment)
        }
        /**
         * 压缩文件
         *
         * @param resFile 待压缩文件
         * @param zipFile 压缩文件
         * @param comment 压缩文件的注释
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO 错误时抛出
         */
        /**
         * 压缩文件
         *
         * @param resFile 待压缩文件
         * @param zipFile 压缩文件
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO 错误时抛出
         */
        @JvmOverloads
        @Throws(IOException::class)
        fun zipFile(
            resFile: File?,
            zipFile: File?,
            comment: String? = null
        ): Boolean {
            if (resFile == null || zipFile == null) {
                return false
            }
            var zos: ZipOutputStream? = null
            return try {
                zos = ZipOutputStream(FileOutputStream(zipFile))
                zipFile(resFile, "", zos, comment)
            } finally {
                if (zos != null) {
                    CloseUtils.Companion.closeIO(zos)
                }
            }
        }

        /**
         * 压缩文件
         *
         * @param resFile  待压缩文件
         * @param rootPath 相对于压缩文件的路径
         * @param zos      压缩文件输出流
         * @param comment  压缩文件的注释
         * @return `true`: 压缩成功<br></br>`false`: 压缩失败
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        private fun zipFile(
            resFile: File?,
            rootPath: String,
            zos: ZipOutputStream,
            comment: String?
        ): Boolean {
            var rootPath = rootPath
            rootPath = rootPath + (if (isSpace(rootPath)) "" else File.separator) + resFile!!.name
            if (resFile.isDirectory) {
                val fileList = resFile.listFiles()
                // 如果是空文件夹那么创建它，我把'/'换为File.separator测试就不成功，eggPain
                if (fileList == null || fileList.size <= 0) {
                    val entry = ZipEntry("$rootPath/")
                    entry.comment = comment
                    zos.putNextEntry(entry)
                    zos.closeEntry()
                } else {
                    for (file in fileList) {
                        // 如果递归返回 false 则返回 false
                        if (!zipFile(file, rootPath, zos, comment)) {
                            return false
                        }
                    }
                }
            } else {
                var `is`: InputStream? = null
                try {
                    `is` = BufferedInputStream(FileInputStream(resFile))
                    val entry = ZipEntry(rootPath)
                    entry.comment = comment
                    zos.putNextEntry(entry)
                    val buffer = ByteArray(BUFFER_LEN)
                    var len: Int
                    while (`is`.read(buffer, 0, BUFFER_LEN).also { len = it } != -1) {
                        zos.write(buffer, 0, len)
                    }
                    zos.closeEntry()
                } finally {
                    CloseUtils.Companion.closeIO(`is`)
                }
            }
            return true
        }

        /**
         * 解压文件
         *
         * @param zipFilePath 待解压文件路径
         * @param destDirPath 目标目录路径
         * @return 文件链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun unzipFile(
            zipFilePath: String?,
            destDirPath: String?
        ): List<File>? {
            return unzipFileByKeyword(zipFilePath, destDirPath, null)
        }

        /**
         * 解压文件
         *
         * @param zipFile 待解压文件
         * @param destDir 目标目录
         * @return 文件链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun unzipFile(
            zipFile: File?,
            destDir: File?
        ): List<File>? {
            return unzipFileByKeyword(zipFile, destDir, null)
        }

        /**
         * 解压带有关键字的文件
         *
         * @param zipFilePath 待解压文件路径
         * @param destDirPath 目标目录路径
         * @param keyword     关键字
         * @return 返回带有关键字的文件链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun unzipFileByKeyword(
            zipFilePath: String?,
            destDirPath: String?,
            keyword: String?
        ): List<File>? {
            return unzipFileByKeyword(
                getFileByPath(zipFilePath),
                getFileByPath(destDirPath),
                keyword
            )
        }

        /**
         * 解压带有关键字的文件
         *
         * @param zipFile 待解压文件
         * @param destDir 目标目录
         * @param keyword 关键字
         * @return 返回带有关键字的文件链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun unzipFileByKeyword(
            zipFile: File?,
            destDir: File?,
            keyword: String?
        ): List<File>? {
            if (zipFile == null || destDir == null) {
                return null
            }
            val files: MutableList<File> = ArrayList()
            val zf = ZipFile(zipFile)
            val entries: Enumeration<*> = zf.entries()
            if (isSpace(keyword)) {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name
                    if (!unzipChildFile(destDir, files, zf, entry, entryName)) {
                        return files
                    }
                }
            } else {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name
                    if (entryName.contains(keyword!!)) {
                        if (!unzipChildFile(destDir, files, zf, entry, entryName)) {
                            return files
                        }
                    }
                }
            }
            return files
        }

        @Throws(IOException::class)
        private fun unzipChildFile(
            destDir: File,
            files: MutableList<File>,
            zf: ZipFile,
            entry: ZipEntry,
            entryName: String
        ): Boolean {
            val filePath = destDir.toString() + File.separator + entryName
            val file = File(filePath)
            files.add(file)
            if (entry.isDirectory) {
                return createOrExistsDir(file)
            } else {
                if (!createOrExistsFile(file)) {
                    return false
                }
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = BufferedInputStream(zf.getInputStream(entry))
                    out = BufferedOutputStream(FileOutputStream(file))
                    val buffer = ByteArray(BUFFER_LEN)
                    var len: Int
                    while (`in`.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                } finally {
                    CloseUtils.Companion.closeIO(`in`, out)
                }
            }
            return true
        }

        /**
         * 获取压缩文件中的文件路径链表
         *
         * @param zipFilePath 压缩文件路径
         * @return 压缩文件中的文件路径链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun getFilesPath(zipFilePath: String?): List<String>? {
            return getFilesPath(getFileByPath(zipFilePath))
        }

        /**
         * 获取压缩文件中的文件路径链表
         *
         * @param zipFile 压缩文件
         * @return 压缩文件中的文件路径链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun getFilesPath(zipFile: File?): List<String>? {
            if (zipFile == null) {
                return null
            }
            val paths: MutableList<String> = ArrayList()
            val entries: Enumeration<*> = ZipFile(zipFile).entries()
            while (entries.hasMoreElements()) {
                paths.add((entries.nextElement() as ZipEntry).name)
            }
            return paths
        }

        /**
         * 获取压缩文件中的注释链表
         *
         * @param zipFilePath 压缩文件路径
         * @return 压缩文件中的注释链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun getComments(zipFilePath: String?): List<String>? {
            return getComments(getFileByPath(zipFilePath))
        }

        /**
         * 获取压缩文件中的注释链表
         *
         * @param zipFile 压缩文件
         * @return 压缩文件中的注释链表
         * @throws IOException IO 错误时抛出
         */
        @Throws(IOException::class)
        fun getComments(zipFile: File?): List<String>? {
            if (zipFile == null) {
                return null
            }
            val comments: MutableList<String> = ArrayList()
            val entries: Enumeration<*> = ZipFile(zipFile).entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                comments.add(entry.comment)
            }
            return comments
        }

        private fun createOrExistsDir(file: File?): Boolean {
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        private fun createOrExistsFile(file: File?): Boolean {
            if (file == null) {
                return false
            }
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

        private fun getFileByPath(filePath: String?): File? {
            return if (isSpace(filePath)) null else File(filePath)
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