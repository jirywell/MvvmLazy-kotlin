package com.rui.mvvmlazy.binding.viewadapter.swiperefresh

/**
 * SmartRefreshLayout控件状态
 */
enum class SmartRefreshState {
    REFRESH_FINISH,  //结束下拉刷新
    LOAD_FINISH,  //结束本次上拉分页加载
    LOAD_FINISH_NOMOREDATA //加载到最后一页面
}