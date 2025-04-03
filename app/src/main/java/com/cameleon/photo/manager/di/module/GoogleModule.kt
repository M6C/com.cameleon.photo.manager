package com.cameleon.photo.manager.di.module

import android.content.Context
import androidx.activity.ComponentActivity
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.di.module.BusinessModule.provideAuthInterceptor
import com.cameleon.photo.manager.di.module.BusinessModule.provideTokenBusiness
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {

    @Provides
    fun provideActivity(@ActivityContext activity: ComponentActivity) = activity

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpClientAuthTokenInterceptor

    @Provides
    fun provideHttpClient(@ApplicationContext context: Context) =
        Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideGoogleSignIn(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))
            .requestIdToken(context.getString(R.string.server_client_id)) // Client ID généré dans Google Cloud
            .requestServerAuthCode(context.getString(R.string.server_client_id), true)
            .build()

    @Provides
    @HttpClientAuthTokenInterceptor
    fun provideHttpClientAuthToken(@ApplicationContext context: Context): OkHttpClient {
        val tokenBusiness = provideTokenBusiness(context)
        val authInterceptor = provideAuthInterceptor(context)
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor { chain: Interceptor.Chain ->
                val authToken = tokenBusiness.getAccessToken()
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken") // Authentification ici
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context) =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            .let { option ->
                GoogleSignIn.getClient(context, option)
            }
}