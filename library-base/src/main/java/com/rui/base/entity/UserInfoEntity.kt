package com.rui.base.entity

import java.io.Serializable

/**
 * ******************************
 * *@Author
 * *date ：
 * *description:用户信息
 * *******************************
 */
class UserInfoEntity(
    var access_token: String,
    var token_type: String,
    var refresh_token: String,
    var expires_in: Long,
    var scope: String
) : Serializable {
    override fun toString(): String {
        return "UserInfoEntity{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", scope='" + scope +
                '}'
    }
}