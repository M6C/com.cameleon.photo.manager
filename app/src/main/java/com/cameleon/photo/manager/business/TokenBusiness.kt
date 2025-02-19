package com.cameleon.photo.manager.business

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.api.GoogleOAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class TokenBusiness @Inject constructor(private val context: Context) {

    companion object {
        private const val PREF_NAME = "secure_prefs"
        const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    private fun getPrefs() = EncryptedSharedPreferences.create(
        PREF_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        getPrefs().edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun reverseToken(tokenKey: String) =
        getPrefs()
            .getString(tokenKey, null)
            ?.reversed()
            ?.also { token ->
                getPrefs().edit().apply {
                    putString(tokenKey, token)
                    apply()
            }
        }

    fun getAccessToken(): String? {
        return getPrefs().getString(ACCESS_TOKEN_KEY, null)
    }

    fun getRefreshToken(): String? {
        return getPrefs().getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearTokens() {
        getPrefs().edit().clear().apply()
    }

    suspend fun refreshAccessToken() : String? {
        val refreshToken: String = getRefreshToken() ?: ""
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GoogleOAuthApi::class.java)

        return withContext(Dispatchers.IO) {
            try {
                val response = api.getTokens(
                    clientId = context.getString(R.string.server_client_id),
                    clientSecret = context.getString(R.string.client_secret),
                    code = refreshToken,
                    grantType = "refresh_token",
                    redirectUri = ""
                )
                saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                response.accessToken
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
