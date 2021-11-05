package com.rui.home

import android.content.Context
import androidx.startup.Initializer
import com.rui.mvvmlazy.utils.common.KLog

/**
 * Created by zjr on 2018/6/21 0021.
 */
class HomeModuleInit : Initializer<Unit> {

    override fun create(context: Context) {
        KLog.d("首页组件初始化")
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}