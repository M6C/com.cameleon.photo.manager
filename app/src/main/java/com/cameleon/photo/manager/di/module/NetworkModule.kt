package com.cameleon.photo.manager.di.module

import android.content.Context
import com.cameleon.photo.manager.api.interceptor.AuthInterceptor
import com.cameleon.photo.manager.di.module.BusinessModule.provideTokenBusiness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


// https://medium.com/@otherTallguy/dagger2-with-mvvm-retrofit-roomdb-in-android-kotlin-3e9eab8875de
// https://medium.com/@hariharanravichandran_25986/dependency-injection-with-dagger-hilt-and-retrofit-4a14ac215935

@Module
//@InstallIn(ActivityComponent::class)
//class NetworkModule {
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @BaseUrlOAuth
    @Provides
    fun provideBaseUrlOAuth(): String = "https://oauth2.googleapis.com/"

    @Provides
    @BaseUrlPhoto
    fun provideBaseUrlPhoto(): String = "https://photoslibrary.googleapis.com/"

    @Provides
    fun provideAuthInterceptor(@ApplicationContext context: Context): AuthInterceptor = AuthInterceptor(provideTokenBusiness(context))

    @Singleton
    @Provides
    @RetrofitOAuth
    fun providesRetrofitOAuth(@BaseUrlOAuth baseUrl: String, interceptor: AuthInterceptor): Retrofit = buildRetrofit(baseUrl, interceptor)
        .also {
            println("-----------------------> providesRetrofitOAuth $it interceptor:${interceptor}")
        }

    @Singleton
    @Provides
    @RetrofitPhoto
    fun providesRetrofitPhoto(@BaseUrlPhoto baseUrl: String, interceptor: AuthInterceptor): Retrofit = buildRetrofit(baseUrl, interceptor)
        .also {
            println("-----------------------> providesRetrofitPhoto $it interceptor:${interceptor}")
        }

    private fun buildRetrofit(baseUrl: String, interceptor: Interceptor): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        okHttpBuilder.addInterceptor(httpLoggingInterceptor)
        okHttpBuilder.addInterceptor(interceptor)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpBuilder.build())
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlOAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlPhoto

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitOAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitPhoto