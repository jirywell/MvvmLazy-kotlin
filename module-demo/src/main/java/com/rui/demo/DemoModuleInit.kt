package com.rui.demo

import android.content.Context
import androidx.startup.Initializer
import com.rui.mvvmlazy.utils.common.KLog

/**
 * Created by zjr on 2018/6/21 0021.
 */
class DemoModuleInit : Initializer<Unit> {

    override fun create(context: Context) {
        KLog.d("demo组件初始化")
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}