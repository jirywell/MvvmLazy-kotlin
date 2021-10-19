package com.rui.mvvmlazy.binding.viewadapter.mswitch

import android.widget.Switch
import androidx.databinding.BindingAdapter

/**
 * Created by zjr on 2020/6/18.
 */
object ViewAdapter {
    /**
     * 设置开关状态
     *
     * @param mSwitch Switch控件
     */
    @JvmStatic
    @BindingAdapter("switchState")
    fun setSwitchState(mSwitch: Switch, isChecked: Boolean) {
        mSwitch.isChecked = isChecked
    }

    /**
     * Switch的状态改变监听
     *
     * @param mSwitch        Switch控件
     * @param changeListener 事件绑定命令
     */
    @JvmStatic
    @BindingAdapter("onSwitchChangeCommand")
    fun onCheckedChangeCommand(mSwitch: Switch, changeListener: (Boolean) -> Unit) {
        mSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            changeListener.invoke(
                isChecked
            )
        }
    }
}