package com.cameleon.photo.manager.business

sealed class GoogleSignInError(val code: Int, val technicalMessage: String) {

    companion object {
        private val list = mutableListOf<GoogleSignInError>()
        fun findByCode(code: Int) = list.find { it.code == code } ?: UNKOWN_ERROR()
    }

    init {
        list.add(this)
    }

    class INTERNET_CONNECTION_ERROR : GoogleSignInError(7, "Internet Connection Error")
    class OAUTH2_CERTIFICATE_ERROR : GoogleSignInError(10, "Certain Google Play services (such as Google Sign-in and App Invites) require you to provide the SHA-1 of your signing certificate so we can create an OAuth2 client and API key for your app\nhttps://console.cloud.google.com/apis/credentials")
    class ACCESS_ERROR_API : GoogleSignInError(12500, "Access/Authorization Error API")
    class ACCESS_BLOCKED_API : GoogleSignInError(12501, "Access Blocked API\nhttps://console.cloud.google.com/apis/api/photoslibrary.googleapis.com")
    class AUTHENTICATION_ALREADY_CALL : GoogleSignInError(12502, "An Other API Authentication Already Running")
    class UNKOWN_ERROR(error : Int = -1, e: RuntimeException? = null) : GoogleSignInError(error, "Sign-in failed - Unknown Code ${e?.message?.run { "message:${this}" }}")
}


class GoogleSignInException(val error : GoogleSignInError) : RuntimeException()