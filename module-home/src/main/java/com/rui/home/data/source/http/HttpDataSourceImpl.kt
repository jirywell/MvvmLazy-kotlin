package com.rui.home.data.source.http

import com.rui.home.data.source.HttpDataSource
import com.rui.home.data.source.http.service.HomeApiService

/**
 * Created by zjr on 2019/3/26.
 */

class HttpDataSourceImpl(val apiService: HomeApiService) : HttpDataSource {
}