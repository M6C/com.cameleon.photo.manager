package com.cameleon.photo.manager.di.module

import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.business.TokenBusiness
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
    fun provideTokenBusiness(): TokenBusiness = TokenBusiness()

    @Provides
    fun provideAuthInterceptor(tokenBusiness: TokenBusiness): AuthInterceptor = AuthInterceptor(tokenBusiness)
}