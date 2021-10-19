package com.rui.demo.ui.viewmodel

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rui.base.entity.ApiResponse
import com.rui.base.ui.viewModel.BasePaginationViewModel
import com.rui.demo.R
import com.rui.demo.data.bean.JokeInfo
import com.rui.demo.data.repository
import com.rui.demo.databinding.TestLayoutItemJokeBinding
import com.rui.mvvmlazy.binding.viewadapter.recyclerview.DataBindingAdapter

class PaginationViewModel : BasePaginationViewModel<JokeInfo>() {
    var myAdapter = object :
        DataBindingAdapter<JokeInfo, TestLayoutItemJokeBinding>(R.layout.test_layout_item_joke) {
        override fun convertItem(
            holder: BaseViewHolder,
            binding: TestLayoutItemJokeBinding?,
            item: JokeInfo
        ) {
            binding!!.entity = item
        }
    }
    override val adapter: BaseQuickAdapter<JokeInfo, BaseViewHolder>
        get() = myAdapter

    /**
     * 接口数据格式通常有两种类型
     * 1.通常有不包含分页信息的,也就是data直接返回list
     * 2. 携带分页信息的,data包含分页信息和数据list
     * 根据接口数据类型选择解析方式
     * @return
     */
    override val dateListType: ListType
        get() = ListType.NO_PAGING_INFO

    override suspend fun getHttpRequestNoPagingData(pageIndex: Int): ApiResponse<List<JokeInfo>> {
        return repository.getJoke(pageIndex, 10, "video")
    }
}