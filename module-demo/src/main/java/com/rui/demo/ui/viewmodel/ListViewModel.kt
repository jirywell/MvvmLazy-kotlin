package com.rui.demo.ui.viewmodel

import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rui.demo.R
import com.rui.demo.data.bean.JokeInfo
import com.rui.demo.databinding.TestLayoutItemJokeBinding
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.binding.viewadapter.recyclerview.DataBindingAdapter
import com.rui.mvvmlazy.utils.common.ScreenUtil.sp2px
import java.util.*

class ListViewModel : BaseViewModel() {
    var type = MutableLiveData(1)
    override fun initData() {
        super.initData()
        val list1: MutableList<JokeInfo> = ArrayList()
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        list1.add(JokeInfo("测试一下", "小明"))
        lineAdapter.setNewInstance(list1)
        val list2: MutableList<JokeInfo> = ArrayList()
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        list2.add(JokeInfo("测试一下", "小明"))
        grideAdapter.setNewInstance(list2)
        val list3: MutableList<JokeInfo> = ArrayList()
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        list3.add(JokeInfo("测试一下", "小明"))
        staggeredAdapter.setNewInstance(list3)
    }

    var lineAdapter = object :
        DataBindingAdapter<JokeInfo, TestLayoutItemJokeBinding>(R.layout.test_layout_item_joke) {
        override fun convertItem(
            holder: BaseViewHolder,
            binding: TestLayoutItemJokeBinding?,
            item: JokeInfo
        ) {
            binding!!.entity = item
        }
    }
    var grideAdapter = object :
        DataBindingAdapter<JokeInfo, TestLayoutItemJokeBinding>(R.layout.test_layout_item_joke) {
        override fun convertItem(
            holder: BaseViewHolder,
            binding: TestLayoutItemJokeBinding?,
            item: JokeInfo
        ) {
            binding!!.entity = item
        }
    }
    var staggeredAdapter= object :
        DataBindingAdapter<JokeInfo, TestLayoutItemJokeBinding>(R.layout.test_layout_item_joke) {
        override fun convertItem(
            holder: BaseViewHolder,
            binding: TestLayoutItemJokeBinding?,
            item: JokeInfo
        ) {
            binding!!.entity = item
            val layoutParams = binding.llParent.layoutParams as LinearLayout.LayoutParams
            if (holder.adapterPosition % 3 == 0) {
                layoutParams.height = sp2px(context, 60f)
            } else if (holder.adapterPosition % 3 == 1) {
                layoutParams.height = sp2px(context, 80f)
            } else {
                layoutParams.height = sp2px(context, 100f)
            }
            binding.llParent.layoutParams = layoutParams
        }
    }

    fun changeType(typeValue: Int) {
        type.value = typeValue
    }
}