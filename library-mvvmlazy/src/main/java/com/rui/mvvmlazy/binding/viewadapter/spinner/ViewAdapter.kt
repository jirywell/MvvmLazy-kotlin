package com.rui.mvvmlazy.binding.viewadapter.spinner

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import java.util.*

/**
 * Created by zjr on 2020/6/18.
 */
object ViewAdapter {
    /**
     * 双向的SpinnerViewAdapter, 可以监听选中的条目,也可以回显选中的值
     *
     * @param spinner        控件本身
     * @param itemDatas      下拉条目的集合
     * @param valueReply     回显的value
     * @param bindingCommand 条目点击的监听
     */
    @JvmStatic
    @BindingAdapter(
        value = ["itemValues", "valueReply", "resource", "dropDownResource", "onItemSelectedCommand"],
        requireAll = false
    )
    fun onItemSelectedCommand(
        spinner: Spinner,
        itemDatas: List<IKeyAndValue>,
        valueReply: String?,
        resource: Int?,
        dropDownResource: Int?,
        bindingCommand: (IKeyAndValue) -> Unit
    ) {
        if (itemDatas == null) {
            throw NullPointerException("this itemDatas parameter is null")
        }
        val lists: MutableList<String?> = ArrayList()
        for (iKeyAndValue in itemDatas) {
            lists.add(iKeyAndValue.key)
        }
        val adapter = ArrayAdapter<String>(
            spinner.context,
            resource ?: android.R.layout.simple_spinner_dropdown_item,
            lists
        )
        adapter.setDropDownViewResource(
            dropDownResource ?: android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val iKeyAndValue = itemDatas[position]
                //将IKeyAndValue对象交给ViewModel
                bindingCommand.invoke(iKeyAndValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //回显选中的值
        if (!TextUtils.isEmpty(valueReply)) {
            for (i in itemDatas.indices) {
                val iKeyAndValue = itemDatas[i]
                if (valueReply == iKeyAndValue.value) {
                    spinner.setSelection(i)
                    return
                }
            }
        }
    }
}