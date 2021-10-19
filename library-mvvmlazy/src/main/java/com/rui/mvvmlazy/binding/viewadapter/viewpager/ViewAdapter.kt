package com.rui.mvvmlazy.binding.viewadapter.viewpager

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Created by zjr on 2020/6/18.
 */
object ViewAdapter {
    @JvmStatic
    @BindingAdapter(
        value = ["onPageScrolledCommand", "onPageSelectedCommand", "onPageScrollStateChangedCommand"],
        requireAll = false
    )
    fun onScrollChangeCommand(
        viewPager: ViewPager,
        onPageScrolledCommand: (ViewPagerDataWrapper) -> Unit,
        onPageSelectedCommand: (Int) -> Unit,
        onPageScrollStateChangedCommand: (Int) -> Unit
    ) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            private var state = 0
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                onPageScrolledCommand.invoke(
                    ViewPagerDataWrapper(
                        position.toFloat(),
                        positionOffset,
                        positionOffsetPixels,
                        state
                    )
                )
            }

            override fun onPageSelected(position: Int) {
                onPageSelectedCommand.invoke(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                this.state = state
                onPageScrollStateChangedCommand.invoke(state)
            }
        })
    }

    class ViewPagerDataWrapper(
        var position: Float,
        var positionOffset: Float,
        var positionOffsetPixels: Int,
        var state: Int
    )
}