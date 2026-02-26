package com.cameleon.photo.manager.di.module

import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GooglePhotosApi
import com.cameleon.photo.manager.api.GoogleUserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    @ApiGoogleOAuth
    fun provideGoogleOAuthApi(@RetrofitOAuth retrofit: Retrofit): GoogleOAuthApi =
            retrofit.create(GoogleOAuthApi::class.java)

    @Provides
    @Singleton
    @ApiGoogleOAuthDirect
    fun provideGoogleOAuthDirectApi(@RetrofitOAuthDirect retrofit: Retrofit): GoogleOAuthApi =
            retrofit.create(GoogleOAuthApi::class.java)

    @Provides
    @Singleton
    fun provideGooglePhotoApi(@RetrofitPhoto retrofit: Retrofit): GooglePhotosApi =
            retrofit.create(GooglePhotosApi::class.java)

    @Provides
    @Singleton
    fun provideGoogleUserApi(@RetrofitGoogleApi retrofit: Retrofit): GoogleUserApi =
            retrofit.create(GoogleUserApi::class.java)
}

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ApiGoogleOAuth

@Retention(AnnotationRetention.BINARY) annotation class ApiGoogleOAuthDirect
