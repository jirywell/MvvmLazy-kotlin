package com.rui.mvvmlazy.http

/**
 * Created by zjr on 2020/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
/**
 * 作者　: zjr
 * 时间　: 2019/12/17
 * 描述　: 服务器返回数据的基类
 * 如果需要框架帮你做脱壳处理请继承它！！请注意：
 * 2.必须实现抽象方法，根据自己的业务判断返回请求结果是否成功
 */
abstract class BaseResponse<T> {

    //抽象方法，用户的基类继承该类时，需要重写该方法
    abstract fun isSuccess(): Boolean

    abstract fun getResponseData(): T

    abstract fun getResponseCode(): Int

    abstract fun getResponseMsg(): String

}