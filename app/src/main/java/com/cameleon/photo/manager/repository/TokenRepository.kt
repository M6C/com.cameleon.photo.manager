package com.cameleon.photo.manager.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.bean.TokenResponse
import javax.inject.Inject

class TokenRepository @Inject constructor(private val context: Context, private val googleOAuthApi: GoogleOAuthApi) {

    companion object {
        private const val PREF_NAME = "secure_prefs"
        const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    suspend fun refreshToken(refreshToken: String): TokenResponse =
        googleOAuthApi.refreshToken(
            clientId = getServerClientId(),
            clientSecret = getClientSecret(),
            refreshToken = refreshToken,
            grantType = "refresh_token"
        )

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        getPrefs().edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun clearTokens() = getPrefs().edit().clear().apply()

    fun getServerClientId() = context.getString(R.string.server_client_id)

    fun getClientSecret() = context.getString(R.string.client_secret)

    fun getAccessToken(): String? = getPrefs().getString(ACCESS_TOKEN_KEY, null)

    fun getRefreshToken() = getPrefs().getString(REFRESH_TOKEN_KEY, null)

    fun showSecretsAndTokens(separator: String = "\n") = "With Secret:$separator${showSecrets(separator)}${separator}With Token:$separator${showTokens(separator)}"

    private fun showSecrets(separator: String = "\n") = "ServerClientId:${getServerClientId()}${separator}ClientSecret:${getClientSecret()}"
    private fun showTokens(separator: String = "\n") = "$ACCESS_TOKEN_KEY : ${getAccessToken()}$separator$REFRESH_TOKEN_KEY : ${getRefreshToken()}"

    private fun getPrefs() = EncryptedSharedPreferences.create(
        PREF_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun reverseToken(tokenKey: String) =
        getPrefs()
            .getString(tokenKey, null)
            ?.reversed()
            ?.also { token ->
                getPrefs().edit().apply {
                    putString(tokenKey, token)
                    apply()
                }
            }
}