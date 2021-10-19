package com.rui.demo.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.TitleBar
import com.rui.base.router.RouterActivityPath
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestActivityTestBinding
import com.rui.demo.ui.viewmodel.TestViewModel
import com.rui.mvvmlazy.base.activity.BaseVmActivity
import com.rui.mvvmlazy.base.activity.BaseVmDbActivity

@Route(path = RouterActivityPath.Test.TESTPAGER)
class TestActivity : BaseVmDbActivity<TestActivityTestBinding, TestViewModel>() {
    override fun initContentView(): Int {
        return R.layout.test_activity_test
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }

    override fun initTitleBar(titleBar: TitleBar?) {
        super.initTitleBar(titleBar)
        titleBar!!.title = "demo测试"
    }
}