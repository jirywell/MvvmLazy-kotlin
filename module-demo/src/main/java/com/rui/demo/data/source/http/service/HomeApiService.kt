package com.rui.demo.data.source.http.service

import com.rui.base.entity.ApiResponse
import com.rui.demo.data.bean.JokeInfo
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ******************************
 * *@Author
 * *date ：
 * *description:接口服务类
 * *******************************
 */
interface HomeApiService {
    @GET("getJoke")
    suspend fun getJoke(
        @Query("page") page: Int,
        @Query("count") count: Int,
        @Query("type") type: String?
    ): ApiResponse<List<JokeInfo>>
}