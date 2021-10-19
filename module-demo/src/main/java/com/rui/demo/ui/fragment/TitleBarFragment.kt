package com.rui.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentTitlebarBinding
import com.rui.demo.ui.viewmodel.TitleBarViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment

class TitleBarFragment : BaseVmDbFragment<TestFragmentTitlebarBinding, TitleBarViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_titlebar
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }
}