package com.rui.mvvmlazy.base

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rui.mvvmlazy.bus.event.SingleLiveEvent
import kotlinx.coroutines.cancel
import java.util.*

/**
 * Created by zjr on 2020/6/15.
 */
open class BaseViewModel : ViewModel(), IBaseViewModel {
    val uc: UIChangeLiveData by lazy {
        UIChangeLiveData()
    }


    fun showDialog(title: String = "请稍后...") {
        uc.showDialogEvent.postValue(title)
    }

    fun dismissDialog() {
        uc.dismissDialogEvent.call()
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>, bundle: Bundle? = null) {
        val params: MutableMap<String, Any> = HashMap()
        params[ParameterField.CLASS] = clz
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        uc.startActivityEvent.postValue(params)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */

    fun startContainerActivity(canonicalName: String, bundle: Bundle? = null) {
        val params: MutableMap<String, Any> = HashMap()
        params[ParameterField.CANONICAL_NAME] = canonicalName
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        uc.startContainerActivityEvent.postValue(params)
    }

    /**
     * 关闭界面
     */
    fun finish() {
        uc.finishEvent.call()
    }

    /**
     * 返回上一层
     */
    fun onBackPressed() {
        uc.onBackPressedEvent.call()
    }

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {}


    override fun onCreate() {}
    override fun onDestroy() {}
    override fun onStart() {}
    override fun onStop() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun initData() {}
    override fun registerEventBus() {}
    override fun removeEventBus() {}
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    inner class UIChangeLiveData {
        val showDialogEvent: SingleLiveEvent<String> by lazy {
            SingleLiveEvent<String>()
        }
        val dismissDialogEvent: SingleLiveEvent<Any> by lazy {
            SingleLiveEvent<Any>()
        }
        val startActivityEvent: SingleLiveEvent<Map<String, Any>> by lazy {
            SingleLiveEvent<Map<String, Any>>()
        }
        val startContainerActivityEvent: SingleLiveEvent<Map<String, Any>> by lazy {
            SingleLiveEvent<Map<String, Any>>()
        }
        val finishEvent: SingleLiveEvent<Any> by lazy {
            SingleLiveEvent<Any>()
        }
        val onBackPressedEvent: SingleLiveEvent<Any> by lazy {
            SingleLiveEvent<Any>()
        }

    }

    object ParameterField {
        var CLASS = "CLASS"
        var CANONICAL_NAME = "CANONICAL_NAME"
        var BUNDLE = "BUNDLE"
    }
}


