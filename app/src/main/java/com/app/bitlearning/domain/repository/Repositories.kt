/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.domain.repository

import com.app.bitlearning.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthToken>
    suspend fun register(request: RegisterRequest): Result<AuthToken>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Flow<Boolean>
}

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun getCourseById(id: String): Result<Course>
    suspend fun getEnrolledCourses(): Result<List<Course>>
    suspend fun getRecommendedCourses(): Result<List<Course>>
    suspend fun searchCourses(query: String): Result<List<Course>>
}

interface LessonRepository {
    suspend fun getLessonsForCourse(courseId: String): Result<List<Lesson>>
    suspend fun markLessonCompleted(lessonId: String): Result<Unit>
}

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateProfile(user: User): Result<User>
    suspend fun getCertificates(): Result<List<Certificate>>
}
