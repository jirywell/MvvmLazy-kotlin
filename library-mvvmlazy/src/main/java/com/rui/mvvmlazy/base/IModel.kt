package com.rui.mvvmlazy.base

/**
 * Created by zjr on 2020/6/15.
 */
interface IModel {
    /**
     * ViewModel销毁时清除Model，与ViewModel共消亡。Model层同样不能持有长生命周期对象
     */
    fun onCleared()
}