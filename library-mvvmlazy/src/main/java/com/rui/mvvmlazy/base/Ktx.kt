package com.rui.mvvmlazy.base

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import androidx.multidex.MultiDex
import androidx.startup.Initializer
import com.rui.mvvmlazy.base.AppManager.Companion.appManager

/**
 * 作者　: zhaojirui
 * 时间　: 2019/12/14
 * 描述　:进行基础组件的初始化操作,并提供全局appContext引用
 */

val appContext: Application by lazy { Ktx.app }

class Ktx : Initializer<Unit> {

    companion object {
        lateinit var app: Application
    }


    override fun create(context: Context) {
        val application = context.applicationContext as Application
        install(application)
    }

    private fun install(application: Application) {
        MultiDex.install(application)
        app = application
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                appManager.addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                appManager.removeActivity(activity)
            }
        })
    }


    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}