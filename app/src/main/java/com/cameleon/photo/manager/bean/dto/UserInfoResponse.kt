package com.cameleon.photo.manager.bean.dto

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("picture") val picture: String?
)
