package com.rui.mvvmlazy.base.activity

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rui.mvvmlazy.base.BaseViewModel

/**
 * 作者　: zjr
 * 时间　: 2019/12/12
 * 描述　: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbActivity<VM : BaseViewModel,DB : ViewDataBinding> : BaseVmActivity<VM>() {

    lateinit var binding: DB

    /**
     * 注入绑定
     */
    override fun initViewDataBinding(): Boolean {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, initContentView())
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel)

        binding.lifecycleOwner = this
        return true
    }

    //刷新布局
    fun refreshLayout() {
        binding.setVariable(viewModelId, viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

}