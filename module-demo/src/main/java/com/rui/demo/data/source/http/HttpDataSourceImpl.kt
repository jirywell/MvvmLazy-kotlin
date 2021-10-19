package com.rui.demo.data.source.http

import com.rui.base.entity.ApiResponse
import com.rui.demo.data.bean.JokeInfo
import com.rui.demo.data.source.HttpDataSource
import com.rui.demo.data.source.http.service.HomeApiService

/**
 * Created by zjr on 2019/3/26.
 */
class HttpDataSourceImpl(var apiService: HomeApiService) : HttpDataSource {
    override suspend fun getJoke(
        page: Int,
        count: Int,
        type: String
    ): ApiResponse<List<JokeInfo>> {
        return apiService.getJoke(page, count, type)
    }


}