package com.rui.mvvmlazy.binding.viewadapter.checkbox

import android.widget.CheckBox
import androidx.databinding.BindingAdapter

/**
 * Created by zjr on 2020/6/16.
 */
object ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */

    @JvmStatic
    @BindingAdapter(value = ["onCheckedChangedCommand"], requireAll = false)
    fun setCheckedChanged(checkBox: CheckBox, bindingCommand: (Boolean) -> Unit) {
        checkBox.setOnCheckedChangeListener { compoundButton, b -> bindingCommand.invoke(b) }
    }
}