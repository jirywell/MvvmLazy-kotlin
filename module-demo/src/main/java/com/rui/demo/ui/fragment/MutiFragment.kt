package com.rui.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentMutiBinding
import com.rui.demo.ui.viewmodel.MutiViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment

class MutiFragment : BaseVmDbFragment<TestFragmentMutiBinding, MutiViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_muti
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }
}