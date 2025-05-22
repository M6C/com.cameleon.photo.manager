package com.cameleon.photo.manager.di.module

import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.api.GooglePhotosApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    @ApiGoogleOAuth
    fun provideGoogleOAuthApi(@RetrofitOAuth retrofit: Retrofit): GoogleOAuthApi = retrofit.create(GoogleOAuthApi::class.java)

    @Provides
    @Singleton
    @ApiGoogleOAuthDirect
    fun provideGoogleOAuthDirectApi(@RetrofitOAuthDirect retrofit: Retrofit): GoogleOAuthApi = retrofit.create(GoogleOAuthApi::class.java)

    @Provides
    @Singleton
    fun provideGooglePhotoApi(@RetrofitPhoto retrofit: Retrofit): GooglePhotosApi = retrofit.create(GooglePhotosApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiGoogleOAuth

@Retention(AnnotationRetention.BINARY)
annotation class ApiGoogleOAuthDirect
