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

    /**
     * Real API: returns [Result.success] with a success message.
     * Mock: auto-signs in (returns success and sets authSuccess).
     */
    suspend fun register(request: RegisterRequest): Result<Unit>

    /**
     * Mobile flow: verify a Google ID Token issued by the Google Sign-In SDK.
     * The [idToken] is obtained from [GoogleSignInAccount.getIdToken()] on the device
     * and sent to `POST /auth/oauth2/google/mobile` on the backend, which verifies it
     * with Google and returns system JWTs.
     * No redirect URI is involved.
     */
    suspend fun loginWithGoogle(idToken: String): Result<AuthToken>

    /**
     * GitHub OAuth2 login via authorization-code flow.
     * [code] is the authorization code from the GitHub browser flow.
     */
    suspend fun loginWithGitHub(code: String): Result<AuthToken>

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
