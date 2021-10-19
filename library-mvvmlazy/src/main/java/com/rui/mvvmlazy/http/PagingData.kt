package com.rui.mvvmlazy.http

/**
 * ******************************
 * *@Author
 * *date ：赵继瑞
 * *description:分页数据类型封装
 * *******************************
 */
class PagingData<T> {
    var records: MutableList<T>? = null
    var total = 0
    var current = 0
    var size = 0
    var searchCount: String? = null
    var pages = 0
}