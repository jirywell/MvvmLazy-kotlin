package com.rui.mvvmlazy.binding.viewadapter.recyclerview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rui.mvvmlazy.binding.viewadapter.recyclerview.LayoutManagers.LayoutManagerFactory
import com.rui.mvvmlazy.binding.viewadapter.recyclerview.LineManagers.LineManagerFactory

/**
 * Created by 赵继瑞 on 2020/3/24
 */
object ViewAdapter {
    //绑定adapter
    @JvmStatic
    @BindingAdapter("bindAdapter")
    fun bindAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        if (adapter != null) recyclerView.adapter = adapter
    }

    //设置分割线
    @JvmStatic
    @BindingAdapter("lineManager")
    fun lineManager(recyclerView: RecyclerView, lineManagerFactory: LineManagerFactory?) {
        lineManagerFactory?.create(recyclerView)
    }

    //设置布局管理器
    @JvmStatic
    @BindingAdapter("layoutManager")
    fun layoutManager(recyclerView: RecyclerView, layoutManagerFactory: LayoutManagerFactory?) {
        if (layoutManagerFactory != null) recyclerView.layoutManager =
            layoutManagerFactory.create(recyclerView)
    }

    //设置滑动监听
    @JvmStatic
    @BindingAdapter(
        value = ["onScrollChangeCommand", "onScrollStateChangedCommand"],
        requireAll = false
    )
    fun onScrollChangeCommand(
        recyclerView: RecyclerView,
        onScrollChangeCommand: (ScrollDataWrapper) -> Unit,
        onScrollStateChangedCommand: (Int) -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var state = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollChangeCommand.invoke(
                    ScrollDataWrapper(
                        dx.toFloat(), dy.toFloat(), state
                    )
                )
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state = newState
                onScrollStateChangedCommand.invoke(newState)
            }
        })
    }

    @BindingAdapter("itemAnimator")
    fun setItemAnimator(recyclerView: RecyclerView, animator: RecyclerView.ItemAnimator?) {
        if (animator != null) recyclerView.itemAnimator = animator
    }

    //设置滑动禁止,开启
    @BindingAdapter("setNestedScrollingEnabled")
    fun setNestedScrollingEnabled(recyclerView: RecyclerView, aBoolean: Boolean?) {
        if (aBoolean != null) recyclerView.isNestedScrollingEnabled = aBoolean
    }

    class ScrollDataWrapper(var scrollX: Float, var scrollY: Float, var state: Int)
}