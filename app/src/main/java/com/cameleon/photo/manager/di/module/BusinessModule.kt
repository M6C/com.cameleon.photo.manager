package com.cameleon.photo.manager.di.module

import android.content.Context
import androidx.activity.ComponentActivity
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.business.GoogleSignInBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import com.cameleon.photo.manager.di.module.GoogleModule.provideGoogleSignIn
import com.cameleon.photo.manager.di.module.GoogleModule.provideHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object BusinessModule {

    @Provides
    @Singleton
    fun provideTokenBusiness(@ApplicationContext context: Context) = TokenBusiness(context)

    @Provides
    @Singleton
    fun provideGoogleSignInBusiness(@ActivityContext activity: ComponentActivity, @ApplicationContext context: Context) = GoogleSignInBusiness(provideHttpClient(context), provideGoogleSignIn(context), provideTokenBusiness(context))

    @Provides
    fun provideAuthInterceptor(@ApplicationContext context: Context) = AuthInterceptor(provideTokenBusiness(context))
}