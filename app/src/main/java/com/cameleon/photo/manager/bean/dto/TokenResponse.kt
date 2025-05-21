package com.cameleon.photo.manager.bean.dto

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("expires_in") val expiresIn: Int?,
    @SerializedName("scope") val scope: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("id_token") val idToken: String?,
    )