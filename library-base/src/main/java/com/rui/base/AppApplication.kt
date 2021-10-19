package com.rui.base

import android.app.Application
import com.rui.base.config.ModuleLifecycleConfig

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化组件(靠前)
        ModuleLifecycleConfig.instance.initModuleAhead(this)
        //....
        //初始化组件(靠后)
        ModuleLifecycleConfig.instance.initModuleLow(this)
    }
}