package com.rui.base.base

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.rui.base.BuildConfig
import com.rui.mvvmlazy.utils.common.KLog
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zzhoujay.richtext.RichText

/**
 * ******************************
 * *@Author
 * *date ：2020/6/2 11:59
 * *description:基础库初始化
 * *******************************
 */
class BaseModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        //开启打印日志
        KLog.init(true)
        //初始化阿里路由框架
        if (BuildConfig.DEBUG) {
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化
        RichText.initCacheDir(application)
        KLog.e("基础层初始化 -- onInitAhead")
        return false
    }

    companion object {
        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(android.R.color.white, android.R.color.black) //全局设置主题颜色
                ClassicsHeader(context).setEnableLastTime(false)
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }


    override fun onInitLow(application: Application): Boolean {
        KLog.e("基础层初始化 -- onInitLow")
        return false
    }
}