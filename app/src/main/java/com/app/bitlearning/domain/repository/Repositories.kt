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
    suspend fun register(request: RegisterRequest): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<AuthToken>
    suspend fun loginWithGitHub(code: String): Result<AuthToken>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Flow<Boolean>
}

interface CourseRepository {
    suspend fun getCourses(page: Int = 0, size: Int = 10): Result<List<Course>>
    suspend fun getCourseById(id: Int): Result<Course>
    suspend fun getCoursesByGrade(grade: Int, page: Int = 0, size: Int = 10): Result<List<Course>>
    suspend fun getEnrolledCourses(page: Int = 0, size: Int = 10): Result<List<Course>>
    suspend fun getMyCourses(page: Int = 0, size: Int = 10): Result<List<Course>>
    suspend fun searchCourses(query: String): Result<List<Course>>
}

interface LessonRepository {
    suspend fun getLessonsForCourse(courseId: Int): Result<List<Lecture>>
    suspend fun markLectureCompleted(lectureId: Int): Result<Unit>
}

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateProfile(user: User): Result<User>
    suspend fun getCertificates(): Result<List<Certificate>>
}
