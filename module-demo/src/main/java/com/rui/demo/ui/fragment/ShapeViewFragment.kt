package com.rui.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentShapeviewBinding
import com.rui.demo.ui.viewmodel.ShapeViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment

class ShapeViewFragment : BaseVmDbFragment<TestFragmentShapeviewBinding, ShapeViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_shapeview
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }
}