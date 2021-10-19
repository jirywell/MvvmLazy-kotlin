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

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.common.KLog
import com.rui.mvvmlazy.utils.common.StringUtils
import com.rui.mvvmlazy.utils.constant.PathConstants
import java.io.File
import java.io.IOException

/**
 * <pre>
 * desc   : 路径相关工具类
 * author : zjr
 * time   : 2018/5/5 上午12:16
</pre> *
 */
class PathUtils private constructor() {
    companion object {
        /**
         * 获取 Android 系统根目录
         * <pre>path: /system</pre>
         *
         * @return 系统根目录
         */
        fun getRootPath(): String {
            return Environment.getRootDirectory().absolutePath
        }

        /**
         * 获取 data 目录
         * <pre>path: /data</pre>
         *
         * @return data 目录
         */
        fun getDataPath(): String {
            return Environment.getDataDirectory().absolutePath
        }

        /**
         * 获取缓存目录
         * <pre>path: data/cache</pre>
         *
         * @return 缓存目录
         */
        fun getIntDownloadCachePath(): String {
            return Environment.getDownloadCacheDirectory().absolutePath
        }
        //===============================内置私有存储空间===========================================//
        /**
         * 获取此应用的缓存目录
         * <pre>path: /data/data/package/cache</pre>
         *
         * @return 此应用的缓存目录
         */
        fun getAppIntCachePath(): String {
            return appContext.cacheDir.absolutePath
        }

        /**
         * 获取此应用的文件目录
         * <pre>path: /data/data/package/files</pre>
         *
         * @return 此应用的文件目录
         */
        fun getAppIntFilesPath(): String {
            return appContext.filesDir.absolutePath
        }

        /**
         * 获取此应用的数据库文件目录
         * <pre>path: /data/data/package/databases/name</pre>
         *
         * @param name 数据库文件名
         * @return 数据库文件目录
         */
        fun getAppIntDbPath(name: String?): String {
            return appContext.getDatabasePath(name).absolutePath
        }
        //===============================外置公共存储空间，这部分需要获取读取权限，并且在Android10以后文件读取都需要使用ContentResolver进行操作===========================================//
        /**
         * 是否是公有目录
         *
         * @return 是否是公有目录
         */
        fun isPublicPath(file: File?): Boolean {
            if (file == null) {
                return false
            }
            try {
                return isPublicPath(file.canonicalPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * 是否是公有目录
         *
         * @return 是否是公有目录
         */
        fun isPublicPath(filePath: String): Boolean {
            return if (StringUtils.isEmpty(filePath)) {
                false
            } else filePath.startsWith(PathConstants.EXT_STORAGE_PATH) && !filePath.startsWith(
                PathConstants.APP_EXT_STORAGE_PATH
            )
        }

        /**
         * 获取 Android 外置储存的根目录
         * <pre>path: /storage/emulated/0</pre>
         *
         * @return 外置储存根目录
         */
        @JvmStatic
        fun getExtStoragePath(): String {
            return Environment.getExternalStorageDirectory().absolutePath
        }

        /**
         * 获取闹钟铃声目录
         * <pre>path: /storage/emulated/0/Alarms</pre>
         *
         * @return 闹钟铃声目录
         */
        fun getExtAlarmsPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)
                .absolutePath
        }

        /**
         * 获取相机拍摄的照片和视频的目录
         * <pre>path: /storage/emulated/0/DCIM</pre>
         *
         * @return 照片和视频目录
         */
        @JvmStatic
        fun getExtDCIMPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .absolutePath
        }

        /**
         * 获取文档目录
         * <pre>path: /storage/emulated/0/Documents</pre>
         *
         * @return 文档目录
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun getExtDocumentsPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .absolutePath
        }

        /**
         * 获取下载目录
         * <pre>path: /storage/emulated/0/Download</pre>
         *
         * @return 下载目录
         */
        @JvmStatic
        fun getExtDownloadsPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath
        }

        /**
         * 获取视频目录
         * <pre>path: /storage/emulated/0/Movies</pre>
         *
         * @return 视频目录
         */
        fun getExtMoviesPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                .absolutePath
        }

        /**
         * 获取音乐目录
         * <pre>path: /storage/emulated/0/Music</pre>
         *
         * @return 音乐目录
         */
        fun getExtMusicPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .absolutePath
        }

        /**
         * 获取提示音目录
         * <pre>path: /storage/emulated/0/Notifications</pre>
         *
         * @return 提示音目录
         */
        fun getExtNotificationsPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)
                .absolutePath
        }

        /**
         * 获取图片目录
         * <pre>path: /storage/emulated/0/Pictures</pre>
         *
         * @return 图片目录
         */
        @JvmStatic
        fun getExtPicturesPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .absolutePath
        }

        /**
         * 获取 Podcasts 目录
         * <pre>path: /storage/emulated/0/Podcasts</pre>
         *
         * @return Podcasts 目录
         */
        fun getExtPodcastsPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
                .absolutePath
        }

        /**
         * 获取铃声目录
         * <pre>path: /storage/emulated/0/Ringtones</pre>
         *
         * @return 下载目录
         */
        fun getExtRingtonesPath(): String {
            return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
                .absolutePath
        }
        //===============================外置内部存储空间，这部分不需要获取读取权限===========================================//
        /**
         * 获取此应用在外置储存中的缓存目录
         * <pre>path: /storage/emulated/0/Android/data/package/cache</pre>
         *
         * @return 此应用在外置储存中的缓存目录
         */
        fun getAppExtCachePath(): String {
            return getFilePath(appContext.externalCacheDir)
        }

        /**
         * 获取此应用在外置储存中的文件目录
         * <pre>path: /storage/emulated/0/Android/data/package/files</pre>
         *
         * @return 此应用在外置储存中的文件目录
         */
        fun getAppExtFilePath(): String {
            return getFilePath(appContext.getExternalFilesDir(null))
        }

        /**
         * 获取此应用在外置储存中的闹钟铃声目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Alarms</pre>
         *
         * @return 此应用在外置储存中的闹钟铃声目录
         */
        fun getAppExtAlarmsPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_ALARMS)
            )
        }

        /**
         * 获取此应用在外置储存中的相机目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/DCIM</pre>
         *
         * @return 此应用在外置储存中的相机目录
         */
        fun getAppExtDCIMPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_DCIM)
            )
        }

        /**
         * 获取此应用在外置储存中的文档目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Documents</pre>
         *
         * @return 此应用在外置储存中的文档目录
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun getAppExtDocumentsPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            )
        }

        /**
         * 获取此应用在外置储存中的下载目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Download</pre>
         *
         * @return 此应用在外置储存中的下载目录
         */
        fun getAppExtDownloadPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            )
        }

        /**
         * 获取此应用在外置储存中的视频目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Movies</pre>
         *
         * @return 此应用在外置储存中的视频目录
         */
        fun getAppExtMoviesPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            )
        }

        /**
         * 获取此应用在外置储存中的音乐目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Music</pre>
         *
         * @return 此应用在外置储存中的音乐目录
         */
        fun getAppExtMusicPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            )
        }

        /**
         * 获取此应用在外置储存中的提示音目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Notifications</pre>
         *
         * @return 此应用在外置储存中的提示音目录
         */
        fun getAppExtNotificationsPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS)
            )
        }

        /**
         * 获取此应用在外置储存中的图片目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Pictures</pre>
         *
         * @return 此应用在外置储存中的图片目录
         */
        fun getAppExtPicturesPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        }

        /**
         * 获取此应用在外置储存中的 Podcasts 目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Podcasts</pre>
         *
         * @return 此应用在外置储存中的 Podcasts 目录
         */
        fun getAppExtPodcastsPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)
            )
        }

        /**
         * 获取此应用在外置储存中的铃声目录
         * <pre>path: /storage/emulated/0/Android/data/package/files/Ringtones</pre>
         *
         * @return 此应用在外置储存中的铃声目录
         */
        fun getAppExtRingtonesPath(): String {
            return getFilePath(
                appContext.getExternalFilesDir(Environment.DIRECTORY_RINGTONES)
            )
        }

        private fun getFilePath(file: File?): String {
            return if (file != null) file.absolutePath else ""
        }

        /**
         * 获取此应用的 Obb 目录
         * <pre>path: /storage/emulated/0/Android/obb/package</pre>
         * <pre>一般用来存放游戏数据包</pre>
         *
         * @return 此应用的 Obb 目录
         */
        fun getObbPath(): String {
            return appContext.obbDir.absolutePath
        }

        /**
         * 将媒体文件转化为资源定位符
         *
         * @param mediaFile 媒体文件
         * @return
         */
        fun getMediaContentUri(mediaFile: File): Uri? {
            return getMediaContentUri(appContext, mediaFile)
        }

        /**
         * 将媒体文件转化为资源定位符
         *
         * @param context
         * @param mediaFile 媒体文件
         * @return
         */
        fun getMediaContentUri(context: Context, mediaFile: File): Uri? {
            val filePath = mediaFile.absolutePath
            val baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val cursor = context.contentResolver.query(
                baseUri,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath),
                null
            )
            return if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                cursor.close()
                Uri.withAppendedPath(baseUri, "" + id)
            } else {
                if (mediaFile.exists()) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, filePath)
                    return context.contentResolver.insert(baseUri, values)
                }
                null
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        fun getDownloadContentUri(context: Context, file: File): Uri? {
            val filePath = file.absolutePath
            val baseUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val cursor = context.contentResolver.query(
                baseUri,
                arrayOf(MediaStore.Downloads._ID),
                MediaStore.Downloads.DATA + "=? ",
                arrayOf(filePath),
                null
            )
            return if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.DownloadColumns._ID))
                cursor.close()
                Uri.withAppendedPath(baseUri, "" + id)
            } else {
                if (file.exists()) {
                    val values = ContentValues()
                    values.put(MediaStore.Downloads.DATA, filePath)
                    return context.contentResolver.insert(baseUri, values)
                }
                null
            }
        }

        /**
         * Return a content URI for a given file.
         *
         * @param file The file.
         * @return a content URI for a given file
         */
        @JvmStatic
        fun getUriForFile(file: File?): Uri? {
            if (file == null) {
                return null
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority = appContext.packageName + ".GlobalUtils.provider"
                FileProvider.getUriForFile(appContext, authority, file)
            } else {
                Uri.fromFile(file)
            }
        }

        /**
         * 根据文件获取uri
         *
         * @param filePath 文件路径
         * @return
         */
        fun getUriByFilePath(filePath: String?): Uri? {
            return if (TextUtils.isEmpty(filePath)) null else getUriByFile(
                File(filePath)
            )
        }

        /**
         * 根据文件获取uri
         *
         * @param file 文件
         * @return
         */
        @JvmStatic
        fun getUriByFile(file: File?): Uri? {
            if (file == null) {
                return null
            }
            return if (isScopedStorageMode() && isPublicPath(
                    file
                )
            ) {
                val filePath = file.absolutePath
                if (filePath.startsWith(PathConstants.EXT_DOWNLOADS_PATH)) {
                    getDownloadContentUri(
                        appContext,
                        file
                    )
                } else if (filePath.startsWith(PathConstants.EXT_PICTURES_PATH) || filePath.startsWith(
                        PathConstants.EXT_DCIM_PATH
                    )
                ) {
                    getMediaContentUri(
                        appContext,
                        file
                    )
                } else {
                    getUriForFile(file)
                }
            } else {
                getUriForFile(file)
            }
        }

        /**
         * 是否是分区存储模式：在公共目录下file的api无效了
         *
         * @return 是否是分区存储模式
         */
        fun isScopedStorageMode(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()
        }

        /**
         * Uri to file.
         *
         * @param uri        The uri.
         * @param columnName The name of the target column.
         *
         * e.g. [MediaStore.Images.Media.DATA]
         * @return file
         */
        fun uri2File(uri: Uri?, columnName: String): File? {
            if (uri == null) {
                return null
            }
            val cl = CursorLoader(appContext)
            cl.uri = uri
            cl.projection = arrayOf(columnName)
            val cursor = cl.loadInBackground()
            val columnIndex = cursor.getColumnIndexOrThrow(columnName)
            cursor.moveToFirst()
            val file = File(cursor.getString(columnIndex))
            cursor.close()
            return file
        }

        /**
         * 根据uri获取文件的绝对路径，解决Android 4.4以上 根据uri获取路径的方法
         *
         * @param uri 资源路径
         * @return 文件路径
         */
        fun getFilePathByUri(uri: Uri?): String? {
            return getFilePathByUri(appContext, uri)
        }

        /**
         * 根据uri获取文件的绝对路径，解决Android 4.4以上 根据uri获取路径的方法
         *
         * @param context 上下文
         * @param uri     资源路径
         * @return 文件路径
         */
        fun getFilePathByUri(context: Context?, uri: Uri?): String? {
            if (context == null || uri == null) {
                return null
            }
            val scheme = uri.scheme
            // 以 file:// 开头的
            if (ContentResolver.SCHEME_FILE == scheme) {
                return uri.path
            }
            // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
            if (ContentResolver.SCHEME_CONTENT == scheme && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                var path: String? = null
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null
                )
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        if (columnIndex > -1) {
                            path = cursor.getString(columnIndex)
                        }
                    }
                    cursor.close()
                }
                return path
            }

            // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
            if (ContentResolver.SCHEME_CONTENT == scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                    context,
                    uri
                )
            ) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    if (split.size == 2) {
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        } else {
                            val storageManager =
                                context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                            try {
                                val storageVolumeClazz =
                                    Class.forName("android.os.storage.StorageVolume")
                                val getVolumeList =
                                    storageManager.javaClass.getMethod("getVolumeList")
                                val getUuid = storageVolumeClazz.getMethod("getUuid")
                                val getState = storageVolumeClazz.getMethod("getState")
                                val getPath = storageVolumeClazz.getMethod("getPath")
                                val isPrimary = storageVolumeClazz.getMethod("isPrimary")
                                val isEmulated = storageVolumeClazz.getMethod("isEmulated")
                                val result = getVolumeList.invoke(storageManager)
                                val length = java.lang.reflect.Array.getLength(result)
                                for (i in 0 until length) {
                                    val storageVolumeElement =
                                        java.lang.reflect.Array.get(result, i)
                                    val mounted = Environment.MEDIA_MOUNTED == getState.invoke(
                                        storageVolumeElement
                                    ) || Environment.MEDIA_MOUNTED_READ_ONLY == getState.invoke(
                                        storageVolumeElement
                                    )
                                    //if the media is not mounted, we need not get the volume details
                                    if (!mounted) {
                                        continue
                                    }
                                    //Primary storage is already handled.
                                    if (isPrimary.invoke(storageVolumeElement) as Boolean
                                        && isEmulated.invoke(storageVolumeElement) as Boolean
                                    ) {
                                        continue
                                    }
                                    val uuid = getUuid.invoke(storageVolumeElement) as String
                                    if (uuid != null && uuid == type) {
                                        return getPath.invoke(storageVolumeElement)
                                            .toString() + "/" + split[1]
                                    }
                                }
                            } catch (ex: Exception) {
                                KLog.d("PathUtils", "$uri parse failed. $ex")
                            }
                        }
                    }
                } else if (isDownloadsDocument(uri)) {
                    val documentId = DocumentsContract.getDocumentId(uri)
                    if (TextUtils.isEmpty(documentId)) {
                        return null
                    }
                    if (documentId.startsWith("raw:")) {
                        return documentId.substring("raw:".length)
                    }
                    if (documentId.startsWith("msf:") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val split = documentId.split(":").toTypedArray()
                        if (split.size == 2) {
                            // content://media/external/downloads
                            val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                            val selection = MediaStore.Downloads._ID + "=?"
                            val selectionArgs = arrayOf(split[1])
                            return getDataColumn(context, contentUri, selection, selectionArgs)
                        }
                    }
                    val id = StringUtils.toLong(documentId, -1)
                    if (id != -1L) {
                        return getDownloadPathById(context, id)
                    }
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = MediaStore.Images.Media._ID + "=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                } else if (isHuaWeiUri(uri)) {
                    val uriPath = uri.path
                    if (!StringUtils.isEmpty(uriPath) && uriPath!!.startsWith("/root")) {
                        return uriPath.replace("/root", "")
                    }
                } else if (isQQUri(uri)) {
                    val uriPath = uri.path
                    if (!StringUtils.isEmpty(uriPath)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + uriPath!!.substring("/QQBrowser".length)
                    }
                }
                return getDataColumn(context, uri, null, null)
            } else if (ContentResolver.SCHEME_FILE.equals(scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        private fun getDownloadPathById(context: Context, id: Long): String? {
            val contentUri =
                ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id)
            return getDataColumn(context, contentUri, null, null)
        }

        private fun getDataColumn(
            context: Context,
            uri: Uri?,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = MediaStore.Images.Media.DATA
            val projection = arrayOf(column)
            try {
                cursor =
                    context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return null
        }

        private const val AUTHORITY_EXTERNAL_STORAGE_DOCUMENT =
            "com.android.externalstorage.documents"
        private const val AUTHORITY_DOWNLOADS_DOCUMENT = "com.android.providers.downloads.documents"
        private const val AUTHORITY_MEDIA_DOCUMENT = "com.android.providers.media.documents"

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return AUTHORITY_EXTERNAL_STORAGE_DOCUMENT == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return AUTHORITY_DOWNLOADS_DOCUMENT == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return AUTHORITY_MEDIA_DOCUMENT == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        /**
         * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
         *
         * @param uri uri The Uri to check.
         * @return Whether the Uri authority is HuaWei Uri.
         */
        fun isHuaWeiUri(uri: Uri): Boolean {
            return "com.huawei.hidisk.fileprovider" == uri.authority
        }

        /**
         * content://com.tencent.mtt.fileprovider/QQBrowser/Android/data/com.xxx.xxx/
         *
         * @param uri uri The Uri to check.
         * @return Whether the Uri authority is QQ Uri.
         */
        fun isQQUri(uri: Uri): Boolean {
            return "com.tencent.mtt.fileprovider" == uri.authority
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}