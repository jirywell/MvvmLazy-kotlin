package com.rui.mvvmlazy.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.rui.mvvmlazy.base.AppManager.Companion.appManager
import com.rui.mvvmlazy.utils.app.ProcessUtils.Companion.killBackgroundProcesses
import com.rui.mvvmlazy.utils.app.ServiceUtils.Companion.stopAllRunningService

/**
 * Created by zjr on 2020/5/14.
 * 全局工具类
 */
class GlobalUtils private constructor() {
    companion object {
        /**
         * 获取主线程的Handler
         *
         * @return 主线程Handler
         */
        /**
         * 主线程Handler
         */
        val mainHandler = Handler(Looper.getMainLooper())

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        /**
         * 初始化工具类
         *
         * @param context 上下文
         */
        @JvmStatic
        fun init(context: Context) {
            Companion.context = context.applicationContext
        }

        /**
         * 获取ApplicationContext
         *
         * @return ApplicationContext
         */
        @JvmStatic
        fun getContext(): Context {
            if (context != null) {
                return context!!
            }
            throw NullPointerException("should be initialized in application")
        }
        //===================全局Handler========================//
        /**
         * 在主线程中执行
         *
         * @param runnable
         * @return
         */
        fun runOnUiThread(runnable: Runnable?): Boolean {
            return mainHandler.post(runnable!!)
        }

        /**
         * 退出程序
         */
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun exitApp() {
            appManager.appExit()
            stopAllRunningService(getContext()!!)
            killBackgroundProcesses(getContext()!!.packageName)
            System.exit(0)
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}