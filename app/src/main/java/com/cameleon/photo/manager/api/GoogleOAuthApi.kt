package com.cameleon.photo.manager.api

import com.cameleon.photo.manager.bean.dto.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleOAuthApi {
        @FormUrlEncoded
        @POST("token")
        suspend fun getTokens(
                @Field("client_id") clientId: String,
                @Field("client_secret") clientSecret: String,
                @Field("code") code: String,
                @Field("grant_type") grantType: String,
                @Field("redirect_uri") redirectUri: String,
                @Field("scope") scope: String? = null
        ): TokenResponse

        @FormUrlEncoded
        @POST("token")
        suspend fun refreshToken(
                @Field("client_id") clientId: String,
                @Field("client_secret") clientSecret: String,
                @Field("refresh_token") refreshToken: String,
                @Field("grant_type") grantType: String
        ): TokenResponse

        @GET("tokeninfo")
        suspend fun getTokenInfo(@Query("access_token") accessToken: String): okhttp3.ResponseBody
}
