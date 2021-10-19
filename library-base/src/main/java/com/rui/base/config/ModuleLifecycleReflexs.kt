package com.rui.base.config

/**
 * ******************************
 * *@Author
 * *date ：zjr
 * *description:组件生命周期反射类名管理，在这里注册需要初始化的组件，通过反射动态调用各个组件的初始化方法,注意：以下模块中初始化的Module类不能被混淆
 * *******************************
 */
object ModuleLifecycleReflexs {
    private const val BaseInit = "com.rui.base.base.BaseModuleInit"
    private const val HomeInit = "com.rui.home.ModuleInit"
    private const val DemoInit = "com.rui.demo.ModuleInit"
    private const val SignInit = "com.rui.sign.SignModuleInit"
    var initModuleNames = arrayOf(BaseInit, HomeInit, DemoInit, SignInit)
}