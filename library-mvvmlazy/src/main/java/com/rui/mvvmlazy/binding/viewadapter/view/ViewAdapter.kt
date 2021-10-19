package com.rui.mvvmlazy.binding.viewadapter.view

import android.os.SystemClock
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.databinding.BindingAdapter

/**
 * Created by zjr on 2020/6/16.
 */
object ViewAdapter {

    /**
     * requireAll 是意思是是否需要绑定全部参数, false为否
     * View的onClick事件绑定
     * onClickCommand 绑定的命令,
     * isThrottleFirst 是否开启防止过快点击
     */
    @JvmStatic
    @BindingAdapter(value = ["onClickCommand", "isThrottleFirst"], requireAll = false)
    fun onClickCommand(view: View, clickCommand: () -> Unit, isThrottleFirst: Boolean) {
        if (isThrottleFirst) {
            view.setOnClickListener { clickCommand.invoke() }
        } else {
            val mHits = LongArray(2)
            view.setOnClickListener {
                System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
                mHits[mHits.size - 1] = SystemClock.uptimeMillis()
                if (mHits[0] < SystemClock.uptimeMillis() - 500) {
                    clickCommand.invoke()
                }
            }
        }
    }


    /**
     * view的onLongClick事件绑定
     */
    @JvmStatic
    @BindingAdapter(value = ["onLongClickCommand"], requireAll = false)
    fun onLongClickCommand(view: View, clickCommand: () -> Unit) {
        view.setOnLongClickListener {
            clickCommand.invoke()
            true
        }

    }

    /**
     * 回调控件本身
     *
     * @param currentView
     * @param bindingCommand
     */
    @JvmStatic
    @BindingAdapter(value = ["currentView"], requireAll = false)
    fun replyCurrentView(currentView: View, bindingCommand: (View) -> Unit) {
        bindingCommand.invoke(currentView)
    }

    /**
     * view是否需要获取焦点
     */
    @JvmStatic
    @BindingAdapter("requestFocus")
    fun requestFocusCommand(view: View, needRequestFocus: Boolean) {
        if (needRequestFocus) {
            view.isFocusableInTouchMode = true
            view.requestFocus()
        } else {
            view.clearFocus()
        }
    }

    /**
     * view的焦点发生变化的事件绑定
     */
    @JvmStatic
    @BindingAdapter("onFocusChangeCommand")
    fun onFocusChangeCommand(view: View, onFocusChangeCommand: (Boolean) -> Unit) {
        view.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus -> onFocusChangeCommand.invoke(hasFocus) }
    }

    /**
     * view的显示隐藏
     */
    @JvmStatic
    @BindingAdapter(value = ["isVisible"], requireAll = false)
    fun isVisible(view: View, visibility: Boolean) {
        if (visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}