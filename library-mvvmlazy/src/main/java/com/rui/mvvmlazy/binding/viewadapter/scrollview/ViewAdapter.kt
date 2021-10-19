package com.rui.mvvmlazy.binding.viewadapter.scrollview

import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter

/**
 * Created by zjr on 2020/6/18.
 */
object ViewAdapter {
    @JvmStatic
    @BindingAdapter("onScrollChangeCommand")
    fun onScrollChangeCommand(
        nestedScrollView: NestedScrollView,
        onScrollChangeCommand: (NestScrollDataWrapper) -> Unit
    ) {
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrollChangeCommand.invoke(
                NestScrollDataWrapper(scrollX, scrollY, oldScrollX, oldScrollY)
            )
        })
    }

    @JvmStatic
    @BindingAdapter("onScrollChangeCommand")
    fun onScrollChangeCommand(
        scrollView: ScrollView,
        onScrollChangeCommand: (ScrollDataWrapper) -> Unit
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            onScrollChangeCommand.invoke(
                ScrollDataWrapper(
                    scrollView.scrollX.toFloat(), scrollView.scrollY.toFloat()
                )
            )
        }
    }

    class ScrollDataWrapper(var scrollX: Float, var scrollY: Float)
    class NestScrollDataWrapper(
        var scrollX: Int,
        var scrollY: Int,
        var oldScrollX: Int,
        var oldScrollY: Int
    )
}