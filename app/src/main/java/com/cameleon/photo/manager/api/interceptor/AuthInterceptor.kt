package com.cameleon.photo.manager.api.interceptor

import android.util.Log
import com.cameleon.photo.manager.business.TokenBusiness
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenBusiness: TokenBusiness) : Interceptor {

    init {
        println("-----------------------> AuthInterceptor interceptor:$this")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenBusiness.getAccessToken() // Récupérer le token
println("----------------------->\n-----------------------> AuthInterceptor interceptor:${this}\n----------------------->    chain:${chain}\n----------------------->    url:${chain.request().url.toUrl()}\n----------------------->    token:${token}\n----------------------->    ${tokenBusiness.showSecretsAndTokens("\n----------------------->\n")}")
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        var response = chain.proceed(requestBuilder.build())

        // Si le token est expiré (401 Unauthorized), on tente de le rafraîchir
        if (response.code == 401) {
            response.close() // Fermer la réponse existante

            Log.i("GooglePhoto", "AuthInterceptor response code : ${response.code} -  Refresh Access Token (Old:$token)")
println("-----------------------> AuthInterceptor response code : ${response.code} -  Refresh Access Token (Old:$token)")

            // Rafraîchir le token (bloquant)
            runBlocking { tokenBusiness.refreshAccessToken() }?.let { token ->
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()

println("-----------------------> AuthInterceptor tokenBusiness.showTokens after refreshAccessToken\n----------------------->    ${tokenBusiness.showSecretsAndTokens("\n----------------------->    ")}")

                Log.i("GooglePhoto", "AuthInterceptor New Access Token : $token")

                response = chain.proceed(newRequest) // Réexécuter la requête avec le nouveau token
            }
        }

        return response
    }
}
