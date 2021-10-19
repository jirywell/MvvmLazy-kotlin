package com.rui.mvvmlazy.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*

object InputTools {
    //隐藏虚拟键盘
    fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
        }
    }

    //显示虚拟键盘
    fun showKeyboard(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED)
    }

    //强制显示或者关闭系统键盘
    fun keyBoard(txtSearchKey: View, status: Boolean) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val m =
                    txtSearchKey.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (status) {
                    m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED)
                } else {
                    m.hideSoftInputFromWindow(txtSearchKey.windowToken, 0)
                }
            }
        }, 300)
    }

    //通过定时器强制隐藏虚拟键盘
    fun timerHideKeyboard(v: View) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                }
            }
        }, 10)
    }

    //输入法是否显示着
    fun keyBoard(edittext: EditText): Boolean {
        var bool = false
        val imm =
            edittext.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            bool = true
        }
        return bool
    }
}