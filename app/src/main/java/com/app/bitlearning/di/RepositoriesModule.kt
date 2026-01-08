/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.di

import com.app.bitlearning.repositories.CameraRepository
import com.app.bitlearning.repositories.CameraRepositoryImpl
import com.app.bitlearning.repositories.MainLog
import com.app.bitlearning.repositories.MainLogImpl
import com.app.bitlearning.repositories.Store
import com.app.bitlearning.repositories.StoreImpl2
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindMainLog(mainLog: MainLogImpl): MainLog

    @Binds
    @Singleton
    abstract fun bindStore(store: StoreImpl2): Store

    @Binds
    @Singleton
    abstract fun bindCameraRepository(cameraRepository: CameraRepositoryImpl): CameraRepository
}
