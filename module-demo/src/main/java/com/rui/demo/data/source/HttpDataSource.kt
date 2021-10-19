package com.rui.demo.data.source

import com.rui.base.entity.ApiResponse
import com.rui.demo.data.bean.JokeInfo
import retrofit2.http.Query

/**
 * Created by zjr on 2019/3/26.
 */
interface HttpDataSource {
    suspend  fun getJoke(
        @Query("page") page: Int,
        @Query("count") count: Int,
        @Query("type") type: String
    ): ApiResponse<List<JokeInfo>>
}