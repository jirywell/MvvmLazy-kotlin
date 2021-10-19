package com.rui.home.ui.activity

import android.os.Bundle
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.rui.home.BR
import com.rui.home.R
import com.rui.home.databinding.HomeActivitySplashBinding
import com.rui.home.ui.viewmodel.SplashViewModel
import com.rui.mvvmlazy.base.activity.BaseVmActivity
import com.rui.mvvmlazy.base.activity.BaseVmDbActivity

class SplashActivity : BaseVmDbActivity<HomeActivitySplashBinding, SplashViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initContentView(): Int {
        return R.layout.home_activity_splash
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()

    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }
}