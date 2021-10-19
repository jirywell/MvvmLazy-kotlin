package com.rui.base.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.*

object ImageDispose {
    /**
     * 将图片内容解析成字节数组
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    @Throws(Exception::class)
    fun readStream(inStream: InputStream): ByteArray {
        val buffer = ByteArray(1024)
        var len = -1
        val outStream = ByteArrayOutputStream()
        while (inStream.read(buffer).also { len = it } != -1) {
            outStream.write(buffer, 0, len)
        }
        val data = outStream.toByteArray()
        outStream.close()
        inStream.close()
        return data
    }

    /**
     * 将字节数组转换为ImageView可调用的Bitmap对象
     * @param bytes
     * @param opts
     * @return Bitmap
     */
    fun getPicFromBytes(
        bytes: ByteArray?,
        opts: BitmapFactory.Options?
    ): Bitmap? {
        return if (bytes != null) if (opts != null) BitmapFactory.decodeByteArray(
            bytes, 0, bytes.size,
            opts
        ) else BitmapFactory.decodeByteArray(bytes, 0, bytes.size) else null
    }

    /**
     * 图片缩放
     * @param bitmap 对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    fun zoomBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
            bitmap, 0, 0, width, height,
            matrix, true
        )
    }

    /**
     * 把Bitmap转Byte
     */
    fun Bitmap2Bytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * 把字节数组保存为一个文件
     */
    fun getFileFromBytes(b: ByteArray?, outputFile: String?): File? {
        var stream: BufferedOutputStream? = null
        var file: File? = null
        try {
            file = File(outputFile)
            val fstream = FileOutputStream(file)
            stream = BufferedOutputStream(fstream)
            stream.write(b)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return file
    }
}