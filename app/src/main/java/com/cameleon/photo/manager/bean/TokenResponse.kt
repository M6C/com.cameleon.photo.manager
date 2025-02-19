package com.cameleon.photo.manager.bean

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("token_type") val tokenType: String
)