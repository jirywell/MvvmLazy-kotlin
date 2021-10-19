package com.rui.base.utils

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.launcher.ARouter
import com.rui.base.R
import com.rui.base.entity.BannerInfo
import com.rui.base.router.RouterActivityPath
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.util.BannerUtils
import com.zzhoujay.richtext.RichText
import com.zzhoujay.richtext.callback.DrawableGetter


/**
 * *******************************
 * *@Author
 * *date ：
 * *description:bind工具类
 * *******************************
 */
object BindUtils {
    @JvmStatic
    @BindingAdapter("bindBannerImg")
    fun bindBannerImg(banner: Banner<BannerInfo, ImageAdapter>, list: List<BannerInfo>) {

        //设置适配器.
        banner.adapter = ImageAdapter(list)
        banner.indicator = RectangleIndicator(banner.context)
        banner.setLoopTime(5000)
        banner.setIndicatorSelectedColor(ContextCompat.getColor(banner.context, R.color.white))
        banner.setIndicatorNormalWidth(BannerUtils.dp2px(12f).toInt())
        banner.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
        banner.setIndicatorRadius(0)
        banner.setOnBannerListener { data, position ->
            data as BannerInfo
            when (data.bannerType) {
                "2" -> {
                    ARouter.getInstance().build(RouterActivityPath.Main.PAGER_WEBVIEW)
                        .withString("url", data.bannerContent).navigation()
                }
                "3" -> {
//                    ARouter.getInstance().build(RouterActivityPath.HOME.LESSION_DETAIL).withString("courseId", data.bannerContent).navigation()
                }
            }
        }
        banner.start()
    }


    @JvmStatic
    @BindingAdapter("bindHtmlText")
    fun bindHtmlText(textView: TextView, str: String?) {
        val PLACE_HOLDER_DRAWABLE_GETTER = DrawableGetter { holder, config, textView ->
            val drawable = ContextCompat.getDrawable(textView.context, R.drawable.ic_def_loading)
            val width = textView.width
            drawable?.setBounds(0, 0, width, width / 2)
            drawable
        }
        str?.let {
            RichText.fromHtml(it) // 设置加载中显示的占位图
                .placeHolder(PLACE_HOLDER_DRAWABLE_GETTER).errorImage(PLACE_HOLDER_DRAWABLE_GETTER)
                .into(textView);
        }
    }


    @JvmStatic
    @BindingAdapter("bindDrawableStart")
    fun bindDrawableStart(tv: TextView, resId: Int) {
        val drawable: Drawable? = ContextCompat.getDrawable(tv.context, resId)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(drawable, null, null, null)
    }

    @JvmStatic
    @BindingAdapter("bindDrawableEnd")
    fun bindDrawableEnd(tv: TextView, resId: Int) {
        val drawable: Drawable? = ContextCompat.getDrawable(tv.context, resId)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, null, drawable, null)
    }

    @JvmStatic
    @BindingAdapter("bindDrawableTop")
    fun bindDrawableTop(tv: TextView, resId: Int) {
        val drawable: Drawable? = ContextCompat.getDrawable(tv.context, resId)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, drawable, null, null)
    }


    @JvmStatic
    @BindingAdapter("bindItemAnimator")
    fun bindItemAnimator(recyclerView: RecyclerView, boolean: Boolean) {
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = boolean
    }


    @JvmStatic
    @BindingAdapter("bindBold")
    fun bindBold(tv: TextView, boolean: Boolean) {
        tv.paint.isFakeBoldText = boolean
    }

    /*给文字添加横线*/
    @JvmStatic
    @BindingAdapter("bindStrike")
    fun bindStrike(tv: TextView, boolean: Boolean) {
        if (boolean)
            tv.paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
    }

    @JvmStatic
    @BindingAdapter("bindTextFlag")
    fun bindTextFlag(tv: TextView, flag: Int) {
        tv.paint.flags = flag
    }

}
