package com.rui.mvvmlazy.binding.viewadapter.image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.rui.mvvmlazy.utils.common.ScreenUtil

/**
 * Created by 赵继瑞 on 2020/6/18.
 */
object ViewAdapter {
    /**
     * 加载普通图片
     *
     * @param imageView      图片控件
     * @param url            图片链接  A file path, or a uri or url
     * @param placeholderRes 占位图

     */
    @JvmStatic
    @BindingAdapter(value = ["bindImgUrl", "placeholderRes"], requireAll = false)
    fun bindImgUrl(imageView: ImageView, url: String?, placeholderRes: Int?) {
        imageView.load(url) {
            crossfade(true)
            placeholder(
                createDefPlaceHolder(
                    imageView.context,
                    placeholderRes,
                    0f
                )
            )
        }
    }

    /**
     * 加载圆形图片
     *
     * @param imageView      图片控件
     * @param url            图片链接  A file path, or a uri or url
     * @param placeholderRes 占位图
     */
    @JvmStatic
    @BindingAdapter(value = ["bindCircleImgUrl", "placeholderRes"], requireAll = false)
    fun bindCircleImgUrl(imageView: ImageView, url: String?, placeholderRes: Int?) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.OVAL
        gradientDrawable.setColor(Color.parseColor("#fff5f5f5"))
        imageView.load(url) {
            crossfade(true)
            transformations(CircleCropTransformation())
            placeholder(
                if (placeholderRes == null) gradientDrawable else ContextCompat.getDrawable(
                    imageView.context,
                    placeholderRes
                )
            )
        }
    }

    /**
     * 加载圆角图片
     *
     * @param imageView      图片控件
     * @param url            图片链接  A file path, or a uri or url
     * @param placeholderRes 占位图
     * @param corners        圆角角度  Sp
     */
    @JvmStatic
    @BindingAdapter(
        value = ["bindCornersImgUrl", "placeholderRes", "bindCorners"],
        requireAll = false
    )
    fun bindCornersImgUrl(
        imageView: ImageView,
        url: String?,
        placeholderRes: Int?,
        corners: Int?,
    ) {
        imageView.load(url) {
            crossfade(true)
            transformations(RoundedCornersTransformation((corners ?: 5).toFloat()))
            placeholder(
                createDefPlaceHolder(
                    imageView.context,
                    placeholderRes,
                    (corners ?: 5).toFloat()
                )
            )
        }
    }

    /**
     * 创建默认占位图
     *
     * @param context        上下文对象
     * @param placeholderRes 图片id
     * @return Drawable对象
     */
    fun createDefPlaceHolder(context: Context?, placeholderRes: Int?, radius: Float): Drawable? {
        if (placeholderRes != null) return ContextCompat.getDrawable(context!!, placeholderRes)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadius = ScreenUtil.dip2px(context!!, radius).toFloat()
        gradientDrawable.setColor(Color.parseColor("#fff5f5f5"))
        return gradientDrawable
    }
}