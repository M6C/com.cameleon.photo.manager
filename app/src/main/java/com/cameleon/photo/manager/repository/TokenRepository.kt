package com.cameleon.photo.manager.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.bean.dto.TokenResponse
import com.cameleon.photo.manager.di.module.ApiGoogleOAuthDirect
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.crypto.AEADBadTagException
import javax.inject.Inject

class TokenRepository @Inject constructor() {

    companion object {
        private const val PREF_NAME = "secure_prefs"
        const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @ApiGoogleOAuthDirect
    lateinit var googleOAuthApi: GoogleOAuthApi

    suspend fun refreshToken(refreshToken: String): TokenResponse =
        googleOAuthApi.refreshToken(
            clientId = getServerClientId(),
            clientSecret = getClientSecret(),
            refreshToken = refreshToken,
            grantType = "refresh_token"
        )

    fun saveTokens(accessToken: String?, refreshToken: String?, retryOnAEADBadTagException: Boolean = true) {
        try {
            getPrefs()?.edit()?.apply {
                putString(ACCESS_TOKEN_KEY, accessToken)
                putString(REFRESH_TOKEN_KEY, refreshToken)
                apply()
            }
        } catch (_: AEADBadTagException) {
            clearTokens()
            if (retryOnAEADBadTagException)
                saveTokens(accessToken, refreshToken, retryOnAEADBadTagException = false)
        }
    }

    fun clearTokens() = getPrefs()?.edit()?.clear()?.apply()

    fun getServerClientId() = context.getString(R.string.server_client_id)

    fun getClientSecret() = context.getString(R.string.client_secret)

    fun getAccessToken(): String? = getPrefsEncrypted(ACCESS_TOKEN_KEY)

    fun getRefreshToken() = getPrefsEncrypted(REFRESH_TOKEN_KEY)

    fun showSecretsAndTokens(separator: String = "\n") = "With Secret:$separator${showSecrets(separator)}${separator}With Token:$separator${showTokens(separator)}"

    private fun showSecrets(separator: String = "\n") = "ServerClientId:${getServerClientId()}${separator}ClientSecret:${getClientSecret()}"
    private fun showTokens(separator: String = "\n") = "$ACCESS_TOKEN_KEY : ${getAccessToken()}$separator$REFRESH_TOKEN_KEY : ${getRefreshToken()}"

    private fun getPrefs(retryOnAEADBadTagException: Boolean = true): SharedPreferences? = try {
        EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: AEADBadTagException) {
        context.deleteSharedPreferences(PREF_NAME)
        if (retryOnAEADBadTagException)
            getPrefs(false)
        else
            null
    }

    private fun getPrefsEncrypted(key: String, default: String? = null, deleteOnAEADBadTagException: Boolean = true): String? = try {
        getPrefs()?.getString(key, default)
    } catch (_: AEADBadTagException) {
        if (deleteOnAEADBadTagException)
            context.deleteSharedPreferences(key)
        null
    }

    fun reverseToken(tokenKey: String) =
        getPrefsEncrypted(tokenKey)
            ?.reversed()
            ?.also { token ->
                getPrefs()?.edit()?.apply {
                    putString(tokenKey, token)
                    apply()
                }
            }
}