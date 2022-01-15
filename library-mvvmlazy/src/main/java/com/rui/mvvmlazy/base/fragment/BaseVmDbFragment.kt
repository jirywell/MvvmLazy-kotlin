package com.rui.mvvmlazy.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rui.mvvmlazy.base.BaseViewModel

/**
 * Created by zjr on 2020/6/15.
 */
abstract class BaseVmDbFragment<VM : BaseViewModel, V : ViewDataBinding> : BaseVmFragment<VM>() {
    lateinit var binding: V
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            initContentView(inflater, container, savedInstanceState),
            container,
            false
        )
        binding.setVariable(viewModelId, viewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    /**
     * =====================================================================
     */
    //刷新布局
    fun refreshLayout() {
        binding.setVariable(viewModelId, viewModel)
    }

}