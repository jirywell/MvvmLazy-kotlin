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
package com.rui.mvvmlazy.utils.display

import android.annotation.TargetApi
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.RenderScript.RSMessageHandler
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.file.CloseUtils
import com.rui.mvvmlazy.utils.file.FileUtils
import java.io.*

/**
 * @author laijian
 * @version 2017/9/18
 * @Copyright (C)下午10:30 , www.hotapk.cn
 * 图片工具类
 */
object ImageUtils {
    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    fun bitmap2Bytes(bitmap: Bitmap?, format: CompressFormat?): ByteArray? {
        if (bitmap == null) return null
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, 100, baos)
        return baos.toByteArray()
    }

    /**
     * yuv转jpeg
     *
     * @param yuvBytes
     * @param width
     * @param height
     * @return
     */
    fun yuv2Jpeg(yuvBytes: ByteArray?, width: Int, height: Int): ByteArray {
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        val baos = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, baos)
        return baos.toByteArray()
    }

    /**
     * 将Bitmap转换成Base64字符串
     *
     * @param bit 图片
     * @return base64 编码的图片
     */
    fun bitmap2StrByBase64(bit: Bitmap?): String? {
        if (bit == null) return null
        val bytes = bitmap2Bytes(bit, CompressFormat.JPEG)
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * Base64字符串转bitmap
     *
     * @param base64
     * @return
     */
    fun base64ToBitmap(base64: String?): Bitmap? {
        val base64ToByte = Base64.decode(base64, Base64.DEFAULT)
        return bytes2Bitmap(base64ToByte)
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format   格式
     * @return 字节数组
     */
    fun drawable2Bytes(drawable: Drawable?, format: CompressFormat?): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap? {
        // 旋转图片 动作
        // 创建新的图片
        var resizedBitmap: Bitmap? = null
        try {
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            resizedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Error) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return resizedBitmap
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.size == 0) null else BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        val bitmap: Bitmap
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1, 1,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * bitmap转drawable
     *
     * @param bitmap bitmap对象
     * @return drawable
     */
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
        return if (bitmap == null) null else BitmapDrawable(
            appContext.resources,
            bitmap
        )
    }

    /**
     * byteArr转drawable
     *
     * @param bytes 字节数组
     * @return drawable
     */
    fun bytes2Drawable(bytes: ByteArray?): Drawable? {
        return bitmap2Drawable(bytes2Bitmap(bytes))
    }

    /**
     * view转bitmap
     *
     * @param view 视图
     * @return bitmap
     */
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(ret)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return ret
    }

    /**
     * 获取bitmap
     *
     * @param file 文件
     * @return bitmap
     */
    fun getBitmap(file: File?): Bitmap? {
        if (file == null) return null
        var `is`: InputStream? = null
        var fileinp: InputStream? = null
        try {
            fileinp = FileUtils.getFileInputStream(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (fileinp != null) {
            `is` = BufferedInputStream(fileinp)
            BitmapFactory.decodeStream(`is`)
        } else {
            null
        }
    }

    /**
     * 获取bitmap
     *
     * @param filePath 文件路径
     * @return bitmap
     */
    fun getBitmap(filePath: String?): Bitmap? {
        return if (isSpace(filePath)) null else BitmapFactory.decodeFile(
            filePath
        )
    }

    /**
     * 获取bitmap
     *
     * @param is 输入流
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?): Bitmap? {
        return if (`is` == null) null else BitmapFactory.decodeStream(`is`)
    }

    /**
     * 获取bitmap
     *
     * @param data   数据
     * @param offset 偏移量
     * @return bitmap
     */
    fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
        return if (data.size == 0) null else BitmapFactory.decodeByteArray(data, offset, data.size)
    }

    /**
     * 获取bitmap
     *
     * @param resId 资源id
     * @return bitmap
     */
    fun getBitmap(@DrawableRes resId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(appContext, resId)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 获取bitmap
     *
     * @param fd 文件描述
     * @return bitmap
     */
    fun getBitmap(fd: FileDescriptor?): Bitmap? {
        return if (fd == null) null else BitmapFactory.decodeFileDescriptor(fd)
    }
    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片
     */
    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    @JvmOverloads
    fun scale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放后的图片
     */
    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放后的图片
     */
    @JvmOverloads
    fun scale(
        src: Bitmap,
        scaleWidth: Float,
        scaleHeight: Float,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setScale(scaleWidth, scaleHeight)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 裁剪图片
     *
     * @param src     源图片
     * @param x       开始坐标x
     * @param y       开始坐标y
     * @param width   裁剪宽度
     * @param height  裁剪高度
     * @param recycle 是否回收
     * @return 裁剪后的图片
     */
    /**
     * 裁剪图片
     *
     * @param src    源图片
     * @param x      开始坐标x
     * @param y      开始坐标y
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    @JvmOverloads
    fun clip(
        src: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src, x, y, width, height)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    fun skew(src: Bitmap, kx: Float, ky: Float, recycle: Boolean): Bitmap? {
        return skew(src, kx, ky, 0f, 0f, recycle)
    }
    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param px      平移因子x
     * @param py      平移因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    /**
     * 倾斜图片
     *
     * @param src 源图片
     * @param kx  倾斜因子x
     * @param ky  倾斜因子y
     * @return 倾斜后的图片
     */
    /**
     * 倾斜图片
     *
     * @param src 源图片
     * @param kx  倾斜因子x
     * @param ky  倾斜因子y
     * @param px  平移因子x
     * @param py  平移因子y
     * @return 倾斜后的图片
     */
    @JvmOverloads
    fun skew(
        src: Bitmap,
        kx: Float,
        ky: Float,
        px: Float = 0f,
        py: Float = 0f,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setSkew(kx, ky, px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @param recycle 是否回收
     * @return 旋转后的图片
     */
    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @return 旋转后的图片
     */
    @JvmOverloads
    fun rotate(src: Bitmap, degrees: Int, px: Float, py: Float, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        if (degrees == 0) return src
        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat(), px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 获取图片旋转角度
     *
     * @param filePath 文件路径
     * @return 旋转角度
     */
    fun getRotateDegree(filePath: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(filePath!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            degree = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 转为圆形图片
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return 圆形图片
     */
    fun toRound(src: Bitmap, recycle: Boolean): Bitmap? {
        return toRound(src, 0, 0, recycle)
    }
    /**
     * 转为圆形图片
     *
     * @param src         源图片
     * @param recycle     是否回收
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @return 圆形图片
     */
    /**
     * 转为圆形图片
     *
     * @param src 源图片
     * @return 圆形图片
     */
    /**
     * 转为圆形图片
     *
     * @param src         源图片
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @return 圆形图片
     */
    @JvmOverloads
    fun toRound(
        src: Bitmap,
        @IntRange(from = 0) borderSize: Int = 0,
        @ColorInt borderColor: Int = 0,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val size = Math.min(width, height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val center = size / 2f
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        rectF.inset((width - size) / 2f, (height - size) / 2f)
        val matrix = Matrix()
        matrix.setTranslate(rectF.left, rectF.top)
        matrix.preScale(size.toFloat() / width, size.toFloat() / height)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        paint.shader = shader
        val canvas = Canvas(ret)
        canvas.drawRoundRect(rectF, center, center, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize.toFloat()
            val radius = center - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        }
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 转为圆角图片
     *
     * @param src     源图片
     * @param radius  圆角的度数
     * @param recycle 是否回收
     * @return 圆角图片
     */
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        recycle: Boolean
    ): Bitmap? {
        return toRoundCorner(src, radius, 0, 0, recycle)
    }
    /**
     * 转为圆角图片
     *
     * @param src         源图片
     * @param radius      圆角的度数
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @param recycle     是否回收
     * @return 圆角图片
     */
    /**
     * 转为圆角图片
     *
     * @param src    源图片
     * @param radius 圆角的度数
     * @return 圆角图片
     */
    /**
     * 转为圆角图片
     *
     * @param src         源图片
     * @param radius      圆角的度数
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @return 圆角图片
     */
    @JvmOverloads
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        @IntRange(from = 0) borderSize: Int = 0,
        @ColorInt borderColor: Int = 0,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        val canvas = Canvas(ret)
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val halfBorderSize = borderSize / 2f
        rectF.inset(halfBorderSize, halfBorderSize)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize.toFloat()
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 添加圆角边框
     *
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param cornerRadius 圆角半径
     * @return 圆角边框图
     */
    fun addCornerBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, false)
    }

    /**
     * 添加圆角边框
     *
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 圆角边框图
     */
    fun addCornerBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle)
    }

    /**
     * 添加圆形边框
     *
     * @param src        源图片
     * @param borderSize 边框尺寸
     * @param color      边框颜色
     * @return 圆形边框图
     */
    fun addCircleBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, false)
    }

    /**
     * 添加圆形边框
     *
     * @param src        源图片
     * @param borderSize 边框尺寸
     * @param color      边框颜色
     * @param recycle    是否回收
     * @return 圆形边框图
     */
    fun addCircleBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, recycle)
    }

    /**
     * 添加边框
     *
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param isCircle     是否画圆
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 边框图
     */
    private fun addBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        isCircle: Boolean,
        cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = if (recycle) src else src.copy(src.config, true)
        val width = ret.width
        val height = ret.height
        val canvas = Canvas(ret)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize.toFloat()
        if (isCircle) {
            val radius = Math.min(width, height) / 2f - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        } else {
            val halfBorderSize = borderSize shr 1
            val rectF = RectF(
                halfBorderSize.toFloat(), halfBorderSize.toFloat(),
                (width - halfBorderSize).toFloat(), (height - halfBorderSize).toFloat()
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        }
        return ret
    }
    /**
     * 添加倒影
     *
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @param recycle          是否回收
     * @return 带倒影图片
     */
    /**
     * 添加倒影
     *
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @return 带倒影图片
     */
    @JvmOverloads
    fun addReflection(
        src: Bitmap,
        reflectionHeight: Int,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        // 原图与倒影之间的间距
        val REFLECTION_GAP = 0
        val srcWidth = src.width
        val srcHeight = src.height
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        val reflectionBitmap = Bitmap.createBitmap(
            src, 0, srcHeight - reflectionHeight,
            srcWidth, reflectionHeight, matrix, false
        )
        val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
        val canvas = Canvas(ret)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP).toFloat(), null)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader: LinearGradient = LinearGradient(
            0f, srcHeight.toFloat(),
            0f, (ret.height + REFLECTION_GAP).toFloat(),
            0x70FFFFFF,
            0x00FFFFFF,
            Shader.TileMode.MIRROR
        )
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(
            0f,
            (srcHeight + REFLECTION_GAP).toFloat(),
            srcWidth.toFloat(),
            ret.height.toFloat(),
            paint
        )
        if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 添加文字水印
     *
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标x
     * @param y        起始坐标y
     * @return 带有文字水印的图片
     */
    fun addTextWatermark(
        src: Bitmap,
        content: String?,
        textSize: Int,
        @ColorInt color: Int,
        x: Float,
        y: Float
    ): Bitmap? {
        return addTextWatermark(src, content, textSize.toFloat(), color, x, y, false)
    }

    /**
     * 添加文字水印
     *
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标x
     * @param y        起始坐标y
     * @param recycle  是否回收
     * @return 带有文字水印的图片
     */
    fun addTextWatermark(
        src: Bitmap,
        content: String?,
        textSize: Float,
        @ColorInt color: Int,
        x: Float,
        y: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src) || content == null) return null
        val ret = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.color = color
        paint.textSize = textSize
        val bounds = Rect()
        paint.getTextBounds(content, 0, content.length, bounds)
        canvas.drawText(content, x, y + textSize, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 添加图片水印
     *
     * @param src       源图片
     * @param watermark 图片水印
     * @param x         起始坐标x
     * @param y         起始坐标y
     * @param alpha     透明度
     * @param recycle   是否回收
     * @return 带有图片水印的图片
     */
    /**
     * 添加图片水印
     *
     * @param src       源图片
     * @param watermark 图片水印
     * @param x         起始坐标x
     * @param y         起始坐标y
     * @param alpha     透明度
     * @return 带有图片水印的图片
     */
    @JvmOverloads
    fun addImageWatermark(
        src: Bitmap,
        watermark: Bitmap?,
        x: Int,
        y: Int,
        alpha: Int,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.copy(src.config, true)
        if (!isEmptyBitmap(watermark)) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.alpha = alpha
            canvas.drawBitmap(watermark!!, x.toFloat(), y.toFloat(), paint)
        }
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 转为alpha位图
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return alpha位图
     */
    /**
     * 转为alpha位图
     *
     * @param src 源图片
     * @return alpha位图
     */
    @JvmOverloads
    fun toAlpha(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.extractAlpha()
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 转为灰度图片
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return 灰度图
     */
    /**
     * 转为灰度图片
     *
     * @param src 源图片
     * @return 灰度图
     */
    @JvmOverloads
    fun toGray(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src.width, src.height, src.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }
    /**
     * 快速模糊图片
     *
     * 先缩小原图，对小图进行模糊，再放大回原先尺寸
     *
     * @param src     源图片
     * @param scale   缩放比例(0...1)
     * @param radius  模糊半径(0...25)
     * @param recycle 是否回收
     * @return 模糊后的图片
     */
    /**
     * 快速模糊
     *
     * 先缩小原图，对小图进行模糊，再放大回原先尺寸
     *
     * @param src    源图片
     * @param scale  缩放比例(0...1)
     * @param radius 模糊半径
     * @return 模糊后的图片
     */
    @JvmOverloads
    fun fastBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) scale: Float,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        var scaleBitmap = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas()
        val filter = PorterDuffColorFilter(
            Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
        )
        paint.colorFilter = filter
        canvas.scale(scale, scale)
        canvas.drawBitmap(scaleBitmap!!, 0f, 0f, paint)
        scaleBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            renderScriptBlur(scaleBitmap, radius, recycle)
        } else {
            stackBlur(scaleBitmap, radius.toInt(), recycle)
        }
        if (scale == 1f) return scaleBitmap
        val ret = Bitmap.createScaledBitmap(scaleBitmap!!, width, height, true)
        if (scaleBitmap != null && !scaleBitmap.isRecycled) scaleBitmap.recycle()
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * renderScript模糊图片
     *
     * API大于17
     *
     * @param src    源图片
     * @param radius 模糊半径(0...25)
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun renderScriptBlur(
        src: Bitmap?,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float
    ): Bitmap? {
        return renderScriptBlur(src, radius, false)
    }

    /**
     * renderScript模糊图片
     *
     * API大于17
     *
     * @param src     源图片
     * @param radius  模糊半径(0...25)
     * @param recycle 是否回收
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun renderScriptBlur(
        src: Bitmap?,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        var rs: RenderScript? = null
        val ret = if (recycle) src else src!!.copy(src.config, true)
        try {
            rs = RenderScript.create(appContext)
            rs.messageHandler = RSMessageHandler()
            val input = Allocation.createFromBitmap(
                rs,
                ret,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            blurScript.setRadius(radius)
            blurScript.forEach(output)
            output.copyTo(ret)
        } finally {
            rs?.destroy()
        }
        return ret
    }
    /**
     * stack模糊图片
     *
     * @param src     源图片
     * @param radius  模糊半径
     * @param recycle 是否回收
     * @return stack模糊后的图片
     */
    /**
     * stack模糊图片
     *
     * @param src    源图片
     * @param radius 模糊半径
     * @return stack模糊后的图片
     */
    @JvmOverloads
    fun stackBlur(src: Bitmap?, radius: Int, recycle: Boolean = false): Bitmap? {
        val ret = if (recycle) src else src!!.copy(src.config, true)
        if (radius < 1) {
            return null
        }
        val w = ret!!.width
        val h = ret.height
        val pix = IntArray(w * h)
        ret.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h)
        return ret
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @param bmp
     * @return
     */
    fun sharpenImageAmeliorate(bmp: Bitmap): Bitmap {
        // 拉普拉斯矩阵
        val laplacian = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)
        val width = bmp.width
        val height = bmp.height
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        var pixR = 0
        var pixG = 0
        var pixB = 0
        var pixColor = 0
        var newR = 0
        var newG = 0
        var newB = 0
        var idx = 0
        val alpha = 0.3f
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + n) * width + k + m]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)
                        newR = newR + (pixR * laplacian[idx] * alpha).toInt()
                        newG = newG + (pixG * laplacian[idx] * alpha).toInt()
                        newB = newB + (pixB * laplacian[idx] * alpha).toInt()
                        idx++
                    }
                }
                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))
                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 图片二值化
     *
     * @param bitmap
     * @return
     */
    fun binaryzation(bitmap: Bitmap): Bitmap? {

        //得到图形的宽度和长度
        val width = bitmap.width
        val height = bitmap.height
        //创建二值化图像
        var binarymap: Bitmap? = null
        binarymap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //依次循环，对图像的像素进行处理
        for (i in 0 until width) {
            for (j in 0 until height) {
                val col = binarymap.getPixel(i, j)
                val alpha = col and -0x1000000
                //得到图像的像素RGB
                val red = col and 0x00FF0000 shr 16
                val green = col and 0x0000FF00 shr 8
                val blue = col and 0x000000FF
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                var gray =
                    (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
                //对图像进行二值化处理
                gray = if (gray <= 110) {
                    0
                } else {
                    255
                }
                // 新的ARGB
                val newColor = alpha or (gray shl 16) or (gray shl 8) or gray
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor)
            }
        }
        return binarymap
    }

    /**
     * 保存图片
     *
     * @param src      源图片
     * @param filePath 要保存到的文件路径
     * @param format   格式
     * @return `true`: 成功<br></br>`false`: 失败
     */
    fun save(src: Bitmap, filePath: String, format: CompressFormat?): Boolean {
        return save(src, getFileByPath(filePath), format, false)
    }

    /**
     * 保存图片
     *
     * @param src      源图片
     * @param filePath 要保存到的文件路径
     * @param format   格式
     * @param recycle  是否回收
     * @return `true`: 成功<br></br>`false`: 失败
     */
    fun save(src: Bitmap, filePath: String, format: CompressFormat?, recycle: Boolean): Boolean {
        return save(src, getFileByPath(filePath), format, recycle)
    }
    /**
     * 保存图片
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return `true`: 成功<br></br>`false`: 失败
     */
    /**
     * 保存图片
     *
     * @param src    源图片
     * @param file   要保存到的文件
     * @param format 格式
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmOverloads
    fun save(src: Bitmap, file: File?, format: CompressFormat?, recycle: Boolean = false): Boolean {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false
        var os: OutputStream? = null
        var ret = false
        try {
            os = BufferedOutputStream(FileOutputStream(file))
            ret = src.compress(format, 100, os)
            if (recycle && !src.isRecycled) src.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(os)
        }
        return ret
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param file 　文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isImage(file: File?): Boolean {
        return file != null && isImage(file.path)
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param filePath 　文件路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isImage(filePath: String): Boolean {
        val path = filePath.toUpperCase()
        return (path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF"))
    }

    /**
     * 获取图片类型
     *
     * @param filePath 文件路径
     * @return 图片类型
     */
    fun getImageType(filePath: String): String? {
        return getImageType(getFileByPath(filePath))
    }

    /**
     * 获取图片类型
     *
     * @param file 文件
     * @return 图片类型
     */
    fun getImageType(file: File?): String? {
        if (file == null) return null
        var `is`: InputStream? = null
        try {
            `is` = FileUtils.getFileInputStream(file)
            if (`is` != null) {
                return getImageType(`is`)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(`is`)
        }
        return null
    }

    /**
     * 流获取图片类型
     *
     * @param is 图片输入流
     * @return 图片类型
     */
    fun getImageType(`is`: InputStream?): String? {
        return if (`is` == null) null else try {
            val bytes = ByteArray(8)
            if (`is`.read(bytes, 0, 8) != -1) getImageType(bytes) else null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取图片类型
     *
     * @param bytes bitmap的前8字节
     * @return 图片类型
     */
    fun getImageType(bytes: ByteArray): String? {
        if (isJPEG(bytes)) return "JPEG"
        if (isGIF(bytes)) return "GIF"
        if (isPNG(bytes)) return "PNG"
        return if (isBMP(bytes)) "BMP" else null
    }

    private fun isJPEG(b: ByteArray): Boolean {
        return b.size >= 2 && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte()
    }

    private fun isGIF(b: ByteArray): Boolean {
        return if (b.size < 6) {
            false
        } else b[0] == 'G'.code.toByte() && b[1] == 'I'.code.toByte() && b[2] == 'F'.code.toByte() && b[3] == '8'.code.toByte() && (b[4] == '7'.code.toByte() || b[4] == '9'.code.toByte()) && b[5] == 'a'.code.toByte()
    }

    private fun isPNG(b: ByteArray): Boolean {
        return (b.size >= 8
                && b[0] == 137.toByte() && b[1] == 80.toByte() && b[2] == 78.toByte() && b[3] == 71.toByte() && b[4] == 13.toByte() && b[5] == 10.toByte() && b[6] == 26.toByte() && b[7] == 10.toByte())
    }

    private fun isBMP(b: ByteArray): Boolean {
        return b.size >= 2 && b[0] == (0x42).toByte() && b[1] == (0x4d).toByte()
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return `true`: 是<br></br>`false`: 否
     */
    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }
    ///////////////////////////////////////////////////////////////////////////
    // 下方和压缩有关
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放压缩后的图片
     */
    fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        return scale(src, newWidth, newHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放压缩后的图片
     */
    fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean): Bitmap? {
        return scale(src, newWidth, newHeight, recycle)
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放压缩后的图片
     */
    fun compressByScale(src: Bitmap, scaleWidth: Float, scaleHeight: Float): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放压缩后的图片
     */
    fun compressByScale(
        src: Bitmap,
        scaleWidth: Float,
        scaleHeight: Float,
        recycle: Boolean
    ): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, recycle)
    }
    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @return 质量压缩后的图片
     */
    @JvmOverloads
    fun compressByQuality(
        src: Bitmap,
        @IntRange(from = 0, to = 100) quality: Int,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, quality, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle     是否回收
     * @return 质量压缩压缩过的图片
     */
    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @return 质量压缩压缩过的图片
     */
    @JvmOverloads
    fun compressByQuality(src: Bitmap, maxByteSize: Long, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) { // 最好质量的不大于最大字节，则返回最佳质量
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) { // 最差质量不小于最大字节，则返回最差质量
                bytes = baos.toByteArray()
            } else {
                // 二分法寻找最佳质量
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @param recycle    是否回收
     * @return 按采样率压缩后的图片
     */
    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @return 按采样率压缩后的图片
     */
    @JvmOverloads
    fun compressBySampleSize(src: Bitmap, sampleSize: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists() && !file.delete()) return false
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
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
     * 计算采样大小
     *
     * @param options   选项
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 采样大小
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        if (maxWidth == 0 || maxHeight == 0) return 1
        var height = options.outHeight
        var width = options.outWidth
        var inSampleSize = 1
        while (1.let { height = height shr it; height } > maxHeight && 1.let {
                width = width shr it; width
            } > maxWidth) {
            inSampleSize = inSampleSize shl 1
        }
        return inSampleSize
    }
}