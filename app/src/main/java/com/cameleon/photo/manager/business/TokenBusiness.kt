package com.cameleon.photo.manager.business

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.ui.activity.MainActivity.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class TokenBusiness @Inject constructor(private val context: Context) {

    companion object {
        private const val PREF_NAME = "secure_prefs"
        const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    fun getServerClientId() = context.getString(R.string.server_client_id)

    fun getClientSecret() = context.getString(R.string.client_secret)

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

    fun showSecrets(separator: String = "\n") = "ServerClientId:${getServerClientId()}${separator}ClientSecret:${getClientSecret()}"
    fun showTokens(separator: String = "\n") = "$ACCESS_TOKEN_KEY : ${getAccessToken()}$separator$REFRESH_TOKEN_KEY : ${getRefreshToken()}"

    suspend fun refreshAccessToken() : String? {
        val refreshToken: String = getRefreshToken() ?: ""
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.e(TAG, "Refresh RefreshToken:$refreshToken")

        val api = retrofit.create(GoogleOAuthApi::class.java)

        return withContext(Dispatchers.IO) {
            try {
                val response = api.refreshToken(
                    clientId = context.getString(R.string.server_client_id),
                    clientSecret = context.getString(R.string.client_secret),
                    refreshToken = refreshToken,
                    grantType = "refresh_token"
                )
                saveTokens(
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

    private fun getPrefs() = EncryptedSharedPreferences.create(
        PREF_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
