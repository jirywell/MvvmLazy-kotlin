package com.rui.base.utils

import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import com.rui.base.R
import com.rui.base.entity.BannerInfo
import com.youth.banner.adapter.BannerAdapter

/**
 * 自定义布局，图片
 */
class ImageAdapter(mDatas: List<BannerInfo>) : BannerAdapter<BannerInfo, ImageHolder>(mDatas) {
    //更新数据
    fun updateData(data: List<BannerInfo?>?) {
        //这里的代码自己发挥，比如如下的写法等等
        mDatas.addAll(data!!)
        notifyDataSetChanged()
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = ImageView(parent.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder, data: BannerInfo, position: Int, size: Int) {
        val params = holder.imageView.layoutParams
        //        val width=ScreenUtil.getScreenWidth(context)-ScreenUtil.dip2px(context,20f)
//        val height= BigDecimal(width.toString()).divide(BigDecimal("2.09"),2, RoundingMode.HALF_UP).toInt()
        holder.imageView.load(Constant.IMAGE_URL + data.bannerPath)
    }
}