package com.cameleon.photo.manager.business

import android.util.Log
import com.cameleon.photo.manager.bean.dto.TokenResponse
import com.cameleon.photo.manager.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class TokenBusiness @Inject constructor() {

    companion object {
        private val TAG = TokenBusiness::class.simpleName
    }

    @Inject
    lateinit var tokenRepository: TokenRepository

    fun getAccessToken() = tokenRepository.getAccessToken()

    fun clearTokens() = tokenRepository.clearTokens()

    fun showSecretsAndTokens(separator: String = "\n") = tokenRepository.showSecretsAndTokens(separator)

    suspend fun refreshAccessToken() : String? {
        val refreshToken: String = tokenRepository.getRefreshToken() ?: ""
        Log.e(TAG, "Refresh RefreshToken:$refreshToken")

        return withContext(Dispatchers.IO) {
            try {
                val response: TokenResponse = tokenRepository.refreshToken(
                    refreshToken = refreshToken,
                )
                tokenRepository.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                response.accessToken
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Refresh AccessToken Failed: HTTP ${e.code()} - $errorBody", e)
                null
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e.message}", e)
                null
            }
        }
    }

    private fun reverseToken() = tokenRepository.reverseToken(TokenRepository.ACCESS_TOKEN_KEY)
}