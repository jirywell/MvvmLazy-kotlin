package com.rui.mvvmlazy.binding.viewadapter.image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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
     * @param centerCrop     是否居中剪切,默认true
     */
    @JvmStatic
    @BindingAdapter(value = ["bindImgUrl", "placeholderRes", "centerCrop"], requireAll = false)
    fun bindImgUrl(imageView: ImageView, url: String?, placeholderRes: Int?, centerCrop: Boolean?) {
        var requestBuilder = Glide.with(imageView.context).asDrawable().load(url)
        if (centerCrop == null || centerCrop) {
            requestBuilder = requestBuilder.centerCrop()
        }
        requestBuilder.apply(
            RequestOptions().placeholder(
                createDefPlaceHolder(
                    imageView.context,
                    placeholderRes,
                    0f
                )
            ).override(imageView.width, imageView.height)
        )
            .into(imageView)
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
        Glide.with(imageView.context).asDrawable().load(url).placeholder(
            if (placeholderRes == null) gradientDrawable else ContextCompat.getDrawable(
                imageView.context,
                placeholderRes
            )
        ).skipMemoryCache(false).override(imageView.width, imageView.height)
            .dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions.bitmapTransform(CircleCrop())).into(imageView)
    }

    /**
     * 加载圆角图片
     *
     * @param imageView      图片控件
     * @param url            图片链接  A file path, or a uri or url
     * @param placeholderRes 占位图
     * @param corners        圆角角度  Sp
     * @param centerCrop     是否居中剪切,默认true
     */
    @JvmStatic
    @BindingAdapter(
        value = ["bindCornersImgUrl", "placeholderRes", "bindCorners", "centerCrop"],
        requireAll = false
    )
    fun bindCornersImgUrl(
        imageView: ImageView,
        url: String?,
        placeholderRes: Int?,
        corners: Int?,
        centerCrop: Boolean?
    ) {
        Glide.with(imageView.context).asDrawable().load(url)
            .placeholder(
                createDefPlaceHolder(
                    imageView.context, placeholderRes,
                    (corners ?: 5).toFloat()
                )
            )
            .override(imageView.width, imageView.height)
            .apply(
                RequestOptions.bitmapTransform(
                    if (centerCrop == null || centerCrop) GlideRoundTransform(
                        imageView.context,
                        corners ?: 5
                    ) else RoundedCorners(
                        ScreenUtil.dip2px(
                            imageView.context,
                            (corners ?: 5f).toFloat()
                        )
                    )
                )
            ).skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
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