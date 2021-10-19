package com.rui.base.base

import android.app.Application

/**
 * ******************************
 * *@Author
 * *date ：
 * *description:动态配置Application，有需要初始化的组件实现该接口，统一在主app的Application中初始化
 * *******************************
 */
interface IModuleInit {
    //初始化优先的
    fun onInitAhead(application: Application): Boolean

    //初始化靠后的
    fun onInitLow(application: Application): Boolean
}