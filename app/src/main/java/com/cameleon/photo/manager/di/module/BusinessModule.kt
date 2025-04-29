package com.cameleon.photo.manager.di.module

import android.content.Context
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.business.GooglePhotoBusiness
import com.cameleon.photo.manager.business.GoogleSignInBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object BusinessModule {

    @Provides
    @Singleton
    fun provideTokenBusiness(@ApplicationContext context: Context): TokenBusiness = TokenBusiness(context)

    @Provides
    @Singleton
    fun provideGoogleSignInBusiness(googleOAuthApi: GoogleOAuthApi, tokenBusiness: TokenBusiness): GoogleSignInBusiness = GoogleSignInBusiness(googleOAuthApi, tokenBusiness)

    @Provides
    @Singleton
    fun providePhotoInBusiness(googlePhotosApi: GooglePhotosApi): GooglePhotoBusiness = GooglePhotoBusiness(googlePhotosApi)

    @Provides
    fun provideAuthInterceptor(tokenBusiness: TokenBusiness): AuthInterceptor = AuthInterceptor(tokenBusiness)
}