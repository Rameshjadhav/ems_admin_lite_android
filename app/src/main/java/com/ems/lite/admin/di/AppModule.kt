package com.ems.lite.admin.di

import android.app.Application
import com.ems.lite.admin.network.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideNetworkService(application: Application): NetworkService {
        return NetworkService(application)
    }
}