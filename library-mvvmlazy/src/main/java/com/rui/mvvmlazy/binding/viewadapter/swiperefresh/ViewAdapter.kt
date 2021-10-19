package com.rui.mvvmlazy.binding.viewadapter.swiperefresh

import androidx.databinding.BindingAdapter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by zjr on 2021/6/30
 * 基于SmartRefreshLayout下拉刷新,分页加载封装实现
 */
object ViewAdapter {
    //是否禁止下拉刷新
    @JvmStatic
    @BindingAdapter("smartEnableRefresh")
    fun setEnableRefresh(smartRefreshLayout: SmartRefreshLayout, aBoolean: Boolean?) {
        if (aBoolean != null) smartRefreshLayout.setEnableRefresh(aBoolean)
    }

    //是否禁止加载更多
    @JvmStatic
    @BindingAdapter("smartEnableLoadMore")
    fun setEnableLoadMore(smartRefreshLayout: SmartRefreshLayout, aBoolean: Boolean?) {
        if (aBoolean != null) smartRefreshLayout.setEnableLoadMore(aBoolean)
    }

    //下拉刷新命令
    @JvmStatic
    @BindingAdapter("smartOnRefreshCommand")
    fun onRefreshCommand(
        smartRefreshLayout: SmartRefreshLayout,
        onRefreshCommand: () -> Unit
    ) {
        smartRefreshLayout.setOnRefreshListener { refreshLayout: RefreshLayout? -> onRefreshCommand.invoke() }
    }

    //下拉刷新命令
    @JvmStatic
    @BindingAdapter(
        value = ["smartOnLoadMoreCommand", "smartEnableAutoLoadMore"],
        requireAll = false
    )
    fun onLoadMoreCommand(
        swipeRefreshLayout: SmartRefreshLayout,
        onLoadMoreCommand: () -> Unit,
        aBoolean: Boolean?
    ) {
        swipeRefreshLayout.setOnLoadMoreListener { onLoadMoreCommand.invoke() }
        if (aBoolean != null) swipeRefreshLayout.setEnableAutoLoadMore(aBoolean)
    }

    //标记SmartRefreshLayout的状态
    @JvmStatic
    @BindingAdapter("smartRefreshState")
    fun smartRefreshState(
        swipeRefreshLayout: SmartRefreshLayout,
        smartRefreshState: SmartRefreshState?
    ) {
        if (smartRefreshState != null) {
            if (smartRefreshState == SmartRefreshState.REFRESH_FINISH) {
                swipeRefreshLayout.finishRefresh()
            } else if (smartRefreshState == SmartRefreshState.LOAD_FINISH) {
                swipeRefreshLayout.finishRefresh()
                swipeRefreshLayout.finishLoadMore()
            } else if (smartRefreshState == SmartRefreshState.LOAD_FINISH_NOMOREDATA) {
                swipeRefreshLayout.finishRefresh()
                swipeRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
    }
}