package com.rui.home.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.rui.base.router.RouterActivityPath
import com.rui.home.BR
import com.rui.home.R
import com.rui.home.databinding.HomeActivityMainBinding
import com.rui.home.ui.viewmodel.MainViewModel
import com.rui.mvvmlazy.base.activity.BaseVmDbActivity

@Route(path = RouterActivityPath.Main.PAGER_MAIN)
class MainActivity : BaseVmDbActivity<MainViewModel,HomeActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initContentView(): Int {
        return R.layout.home_activity_main
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }
}