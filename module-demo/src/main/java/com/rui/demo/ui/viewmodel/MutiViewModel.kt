package com.rui.demo.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.widget.MultiStateView.ViewState

class MutiViewModel : BaseViewModel() {
    //多状态布局控制
    var viewState = MutableLiveData<ViewState>()

    /**
     * 重新加载数据
     */
    var reTryLoad: () -> Unit = {
        viewState.setValue(
            ViewState.CONTENT
        )
    }
    var contentClick: () -> Unit = {
        viewState.setValue(
            ViewState.CONTENT
        )
    }
    var loadingClick: () -> Unit = {
        viewState.setValue(
            ViewState.LOADING
        )
    }
    var emptyClick: () -> Unit = {
        viewState.setValue(
            ViewState.EMPTY
        )
    }
    var errorClick: () -> Unit = {
        viewState.setValue(
            ViewState.ERROR
        )
    }

}