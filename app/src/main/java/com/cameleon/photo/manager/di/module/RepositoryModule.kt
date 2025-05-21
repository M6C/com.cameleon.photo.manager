package com.cameleon.photo.manager.di.module

import com.cameleon.photo.manager.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTokenRepository(): TokenRepository = TokenRepository()
}