package com.cameleon.photo.manager.di.module

import android.content.Context
import com.cameleon.photo.manager.api.GoogleOAuthApi
import com.cameleon.photo.manager.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTokenRepository(@ApplicationContext context: Context, @ApiGoogleOAuthDirect googleOAuthApi: GoogleOAuthApi): TokenRepository = TokenRepository(context, googleOAuthApi)
}