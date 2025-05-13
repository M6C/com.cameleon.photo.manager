package com.cameleon.photo.manager.di.module

import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.business.GooglePhotoBusiness
import com.cameleon.photo.manager.business.GoogleSignInBusiness
import com.cameleon.photo.manager.business.TokenBusiness
import com.cameleon.photo.manager.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object BusinessModule {

    @Provides
    @Singleton
    fun provideTokenBusiness(tokenRepository: TokenRepository): TokenBusiness = TokenBusiness(tokenRepository)

    @Provides
    @Singleton
    fun provideGoogleSignInBusiness(@ApiGoogleOAuth googleOAuthApi: GoogleOAuthApi, tokenRepository: TokenRepository): GoogleSignInBusiness = GoogleSignInBusiness(googleOAuthApi, tokenRepository)

    @Provides
    @Singleton
    fun providePhotoInBusiness(googlePhotosApi: GooglePhotosApi): GooglePhotoBusiness = GooglePhotoBusiness(googlePhotosApi)

    @Provides
    fun provideAuthInterceptor(tokenBusiness: TokenBusiness): AuthInterceptor = AuthInterceptor(tokenBusiness)
}