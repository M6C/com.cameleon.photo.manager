package com.cameleon.photo.manager.di.module

import android.content.Context
import androidx.activity.ComponentActivity
import com.cameleon.photo.manager.R
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.business.TokenBusiness
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {

        @Provides fun provideActivity(@ActivityContext activity: ComponentActivity) = activity

        @Provides fun provideContext(@ApplicationContext context: Context) = context

        @Qualifier
        @Retention(AnnotationRetention.BINARY)
        annotation class HttpClientAuthTokenInterceptor

        @Provides
        fun provideGoogleSignIn(@ApplicationContext context: Context): GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary"))
                        .requestServerAuthCode(context.getString(R.string.server_client_id), true)
                        .build()

        @Provides
        @HttpClientAuthTokenInterceptor
        fun provideHttpClientAuthToken(
                authInterceptor: AuthInterceptor,
                tokenBusiness: TokenBusiness
        ): OkHttpClient {
                return OkHttpClient.Builder().addInterceptor(authInterceptor).build()
        }

        @Provides
        fun provideGoogleSignInClient(
                @ApplicationContext context: Context,
                options: GoogleSignInOptions
        ) = GoogleSignIn.getClient(context, options)
}
