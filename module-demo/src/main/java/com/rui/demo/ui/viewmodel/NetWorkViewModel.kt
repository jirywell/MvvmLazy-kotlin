package com.rui.demo.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.rui.demo.data.bean.JokeInfo
import com.rui.demo.data.repository
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.ext.request
import com.rui.mvvmlazy.state.ResultState

class NetWorkViewModel : BaseViewModel() {
    var jokeInfo = MutableLiveData<ResultState<List<JokeInfo>>>()
    var netDataStr = MutableLiveData<String>()
    var clickCommand1: () -> Unit = {
        getNetworkData1()
    }
    var clickCommand2: () -> Unit = {
        getNetworkData2()
    }

    /**
     * 在viewModel回调处理结果
     */
    private fun getNetworkData1() {
        request({ repository.getJoke(1, 10, "video") }, {
            netDataStr.value = Gson().toJson(it)
        }, {

        }, isShowDialog = true, loadingMessage = "加载中,请稍后..")
    }

    /**
     * 将请求数据结果传给 MutableLiveData,通过页面检测处理结果
     */
    private fun getNetworkData2() {
        request(
            { repository.getJoke(1, 10, "video") },
            jokeInfo,
            isShowDialog = true,
            loadingMessage = "加载中,请稍后.."
        )
    }
}