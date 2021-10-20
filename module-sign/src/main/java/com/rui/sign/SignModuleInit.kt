package com.rui.sign

import com.rui.base.base.IModuleInit
import android.app.Application
import com.rui.mvvmlazy.utils.common.KLog

/**
 * Created by zjr on 2018/6/21 0021.
 */
class SignModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        KLog.e("登录模块初始化 -- onInitAhead")
        return false
    }

    override fun onInitLow(application: Application): Boolean {
        KLog.e("登录模块初始化 -- onInitLow")
        return false
    }
}