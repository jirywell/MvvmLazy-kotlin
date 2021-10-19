package com.rui.home.data.source.local

import com.rui.home.data.source.LocalDataSource
import com.rui.mvvmlazy.utils.data.SPUtils

/**
 * 本地数据源，可配合Room框架使用
 * Created by zjr on 2019/3/26.
 */


class LocalDataSourceImpl : LocalDataSource {
    override fun saveUserName(userName: String) {
        SPUtils.instance.put("UserName", userName)
    }

    override fun savePassword(password: String) {
        SPUtils.instance.put("password", password)
    }

    override fun getUserName(): String {
        return SPUtils.instance.getString("UserName")!!
    }

    override fun getPassword(): String {
        return SPUtils.instance.getString("password")!!
    }

}