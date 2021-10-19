package com.rui.mvvmlazy.binding.viewadapter.recyclerview

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 文件名：DataBindingAdapter
 * 描  述：增加对databinding的支持
 * 作  者：zjr
 * 时  间：2018/1/10
 * 邮  箱：
 * 版  权：
 */
abstract class DataBindingAdapter<T, V : ViewDataBinding>(layoutResId: Int) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutResId) {

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        DataBindingUtil.bind<ViewDataBinding>(viewHolder.itemView)
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        val binding = DataBindingUtil.getBinding<V>(holder.itemView)
        convertItem(holder, binding, item)
        binding?.executePendingBindings()

    }

    protected abstract fun convertItem(holder: BaseViewHolder, binding: V?, item: T)
}
