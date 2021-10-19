package com.rui.home

import com.rui.mvvmlazy.utils.common.KLog.e
import com.rui.base.base.IModuleInit
import android.app.Application
import com.rui.mvvmlazy.utils.common.KLog

/**
 * Created by zjr on 2018/6/21 0021.
 */
class ModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        e("工作模块初始化 -- onInitAhead")
        return false
    }

    override fun onInitLow(application: Application): Boolean {
        e("工作模块初始化 -- onInitLow")
        return false
    }
}