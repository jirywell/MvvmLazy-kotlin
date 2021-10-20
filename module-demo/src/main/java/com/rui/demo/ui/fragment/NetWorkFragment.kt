package com.rui.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentNetWorkBinding
import com.rui.demo.ui.viewmodel.NetWorkViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment
import com.rui.mvvmlazy.ext.parseState
import com.rui.mvvmlazy.utils.common.ToastUtils

class NetWorkFragment : BaseVmDbFragment<TestFragmentNetWorkBinding, NetWorkViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_net_work
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.jokeInfo.observe(this, { resultState ->
            parseState(resultState, {
                viewModel.netDataStr.value = Gson().toJson(it)
            }, {
                ToastUtils.showShort(it.errorMsg)
            })
        })
    }
}