package com.rui.base.debug

import android.app.Application
import com.rui.base.config.ModuleLifecycleConfig

/**
 * Created by zjr on 2018/6/25 0025.
 * debug包下的代码不参与编译，仅作为独立模块运行时初始化数据
 */
class DebugApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化组件(靠前)
        ModuleLifecycleConfig.instance.initModuleAhead(this)
        //....
        //初始化组件(靠后)
        ModuleLifecycleConfig.instance.initModuleLow(this)
    }
}