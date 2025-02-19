package com.cameleon.photo.manager.business

import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

interface IGoogleAuthBusiness {
    fun createGooglePhotosApi(authToken: String): GooglePhotosApi
    suspend fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, clientId: String, clientSecret: String, onSignIn: () -> Unit)
}

class GoogleAuthBusiness @Inject constructor(private val clientHttp: Retrofit, private val tokenBusiness: TokenBusiness) : IGoogleAuthBusiness {

    override fun createGooglePhotosApi(authToken: String): GooglePhotosApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenBusiness))
            .addInterceptor { chain: Interceptor.Chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken") // Authentification ici
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://photoslibrary.googleapis.com/") // Base URL Google Photos
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePhotosApi::class.java)
    }

    override suspend fun exchangeAuthCodeForTokens(account: GoogleSignInAccount, clientId: String, clientSecret: String, onSignIn: () -> Unit) {
        val authCode = account.serverAuthCode
        if (authCode == null)
            return

        val api = clientHttp.create(GoogleOAuthApi::class.java)

        val response = api.getTokens(
            clientId = clientId,
            clientSecret = clientSecret,
            code = authCode,
            grantType = "authorization_code",
            redirectUri = ""
        )
        val accessToken = response.accessToken
        val refreshToken = response.refreshToken
        tokenBusiness.saveTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )

        onSignIn()
    }
}