/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.app.bitlearning.data.repository.AuthRepositoryImpl
import com.app.bitlearning.data.repository.CourseRepositoryImpl
import com.app.bitlearning.data.repository.LessonRepositoryImpl
import com.app.bitlearning.data.repository.UserRepositoryImpl
import com.app.bitlearning.domain.repository.AuthRepository
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.LessonRepository
import com.app.bitlearning.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(impl: CourseRepositoryImpl): CourseRepository

    @Binds
    @Singleton
    abstract fun bindLessonRepository(impl: LessonRepositoryImpl): LessonRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
