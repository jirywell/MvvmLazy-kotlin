package com.rui.mvvmlazy.utils.display

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Environment
import android.view.View
import com.rui.mvvmlazy.utils.common.KLog.e
import com.rui.mvvmlazy.utils.common.ToastUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * *******************************
 * *@Author  赵继瑞
 * *date ：2020/3/10 11:59
 * *description:截屏
 * *******************************
 */
object ShotShareUtil {
    /**
     * 获取截屏
     */
    fun viewShot(view: View): String? {
        var imagePath: String? = null
        val width = view.width
        val height = view.height
        //设置缓存
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        var bmp = view.drawingCache
        if (bmp != null) {
            bmp = Bitmap.createBitmap(bmp, 0, 0, width, height)
            try {
                // 图片文件路径
                imagePath =
                    Environment.getExternalStorageDirectory().absolutePath + "/" + SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"
                    ).format(
                        Date()
                    ) + "share.png"
                e("====imagePath====$imagePath")
                val file = File(imagePath)
                val os = FileOutputStream(file)
                bmp.compress(CompressFormat.PNG, 100, os)
                os.flush()
                os.close()
                ToastUtils.showShort("海报保存至$imagePath")
                return imagePath
            } catch (e: Exception) {
                e("====screenshot:error====" + e.message)
            }
        }
        return null
    }
}