package com.rui.mvvmlazy.ext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.base.activity.BaseVmActivity
import com.rui.mvvmlazy.base.fragment.BaseVmFragment
import com.rui.mvvmlazy.ext.util.loge
import com.rui.mvvmlazy.http.AppException
import com.rui.mvvmlazy.http.BaseResponse
import com.rui.mvvmlazy.http.ExceptionHandle
import com.rui.mvvmlazy.state.ResultState
import com.rui.mvvmlazy.state.paresException
import com.rui.mvvmlazy.state.paresResult
import kotlinx.coroutines.*


/**
 * 作者　: zjr
 * 时间　: 2020/4/8
 * 描述　:BaseViewModel请求协程封装
 */

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param resultState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseVmActivity<*>.parseState(
    resultState: ResultState<T>,
    onSuccess: (T) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: ((message: String) -> Unit)? = null
) {
    when (resultState) {
        is ResultState.Loading -> {
            if (onLoading == null) {
                showDialog(resultState.loadingMessage)
            } else {
                onLoading.invoke(resultState.loadingMessage)
            }
        }
        is ResultState.Success -> {
            dismissDialog()
            onSuccess(resultState.data)
        }
        is ResultState.Error -> {
            dismissDialog()
            onError?.run { this(resultState.error) }
        }
    }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param resultState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseVmFragment<*>.parseState(
    resultState: ResultState<T>,
    onSuccess: (T) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: ((message: String) -> Unit)? = null
) {
    when (resultState) {
        is ResultState.Loading -> {
            if (onLoading == null) {
                showDialog(resultState.loadingMessage)
            } else {
                onLoading.invoke(resultState.loadingMessage)
            }
        }
        is ResultState.Success -> {
            dismissDialog()
            onSuccess(resultState.data)
        }
        is ResultState.Error -> {
            dismissDialog()
            onError?.run { this(resultState.error) }
        }
    }
}


/**
 * net request 过滤服务器结果，失败抛异常
 * @param block 请求体方法
 * @param resultState 请求回调的ResultState数据
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.request(
    block: suspend () -> BaseResponse<T>,
    resultState: MutableLiveData<ResultState<T>>,
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) resultState.value = ResultState.onAppLoading(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            resultState.paresResult(it)
        }.onFailure {
            it.message?.loge()
            resultState.paresException(it)
        }
    }
}

/**
 * net request 不校验请求结果数据是否是成功
 * @param block 请求体方法
 * @param resultState 请求回调的ResultState数据
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.requestNoCheck(
    block: suspend () -> T,
    resultState: MutableLiveData<ResultState<T>>,
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) resultState.value = ResultState.onAppLoading(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            resultState.paresResult(it)
        }.onFailure {
            it.message?.loge()
            resultState.paresException(it)
        }
    }
}

/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不传
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.request(
    block: suspend () -> BaseResponse<T>,
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) showDialog(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            dismissDialog()
            runCatching {
                //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                executeResponse(it) { t ->
                    success(t)
                }
            }.onFailure { e ->
                //打印错误消息
                e.message?.loge()
                //失败回调
                error(ExceptionHandle.handleException(e))
            }
        }.onFailure {
            //网络请求异常 关闭弹窗
            dismissDialog()
            //打印错误消息
            it.message?.loge()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 *  不过滤请求结果
 * @param block 请求体 必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不给
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.requestNoCheck(
    block: suspend () -> T,
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    if (isShowDialog) showDialog(loadingMessage)
    return viewModelScope.launch {
        runCatching {
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            dismissDialog()
            //成功回调
            success(it)
        }.onFailure {
            //网络请求异常 关闭弹窗
            dismissDialog()
            //打印错误消息
            it.message?.loge()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
 */
suspend fun <T> executeResponse(
    response: BaseResponse<T>,
    success: suspend CoroutineScope.(T) -> Unit
) {
    coroutineScope {
        when {
            response.isSuccess() -> {
                success(response.getResponseData())
            }
            else -> {
                throw AppException(
                    response.getResponseCode(),
                    response.getResponseMsg(),
                    response.getResponseMsg()
                )
            }
        }
    }
}

/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param success 成功回调
 * @param error 失败回调 可不给
 */
fun <T> BaseViewModel.launch(
    block: () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit = {}
) {
    viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}
