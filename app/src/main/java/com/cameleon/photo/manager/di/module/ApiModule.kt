package com.cameleon.photo.manager.di.module

import android.content.Context
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.di.module.GoogleModule.provideHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideGoogleOAuthApi(@ApplicationContext context: Context) = provideHttpClient(context).create(GoogleOAuthApi::class.java)
}