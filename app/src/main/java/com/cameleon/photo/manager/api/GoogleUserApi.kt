package com.cameleon.photo.manager.api

import com.cameleon.photo.manager.bean.dto.UserInfoResponse
import retrofit2.http.GET

interface GoogleUserApi {
    @GET("oauth2/v2/userinfo") suspend fun getUserInfo(): UserInfoResponse
}
