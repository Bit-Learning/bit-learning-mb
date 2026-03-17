/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.repository

import com.app.bitlearning.core.network.BitLearningApiService
import com.app.bitlearning.data.mapper.toDomain
import com.app.bitlearning.domain.model.*
import com.app.bitlearning.domain.repository.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// ─────────────────────────────────────────────
//  Auth Repository Implementation (Mock)
// ─────────────────────────────────────────────
@Singleton
class MockAuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val isLoggedInState = MutableStateFlow(false)

    override suspend fun login(request: LoginRequest): Result<AuthToken> {
        delay(1200) // Simulate network
        return if (request.email.isNotBlank() && request.password.length >= 6) {
            isLoggedInState.value = true
            Result.success(
                AuthToken(
                    accessToken = "mock_access_token_${System.currentTimeMillis()}",
                    refreshToken = "mock_refresh_token",
                    expiresAt = System.currentTimeMillis() + 3600_000,
                ),
            )
        } else {
            Result.failure(Exception("Email hoặc mật khẩu không đúng"))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        delay(1500)
        return if (request.email.contains("@") && request.password.length >= 6) {
            // Mock auto-logs in after registration (no email activation step)
            isLoggedInState.value = true
            Result.success(Unit)
        } else {
            Result.failure(Exception("Thông tin đăng ký không hợp lệ"))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<AuthToken> {
        delay(1000)
        isLoggedInState.value = true
        return Result.success(
            AuthToken(
                accessToken = "mock_google_token_${System.currentTimeMillis()}",
                refreshToken = "mock_refresh",
                expiresAt = System.currentTimeMillis() + 3600_000,
            ),
        )
    }

    override suspend fun loginWithGitHub(code: String): Result<AuthToken> {
        delay(1000)
        isLoggedInState.value = true
        return Result.success(
            AuthToken(
                accessToken = "mock_github_token_${System.currentTimeMillis()}",
                refreshToken = "mock_refresh",
                expiresAt = System.currentTimeMillis() + 3600_000,
            ),
        )
    }

    override suspend fun scanQrToken(qrToken: String): Result<Unit> {
        // In mock mode just simulate a short delay and succeed.
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun confirmQrLogin(qrToken: String): Result<Unit> {
        // Mock confirmation: no-op with small delay.
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun logout() {
        isLoggedInState.value = false
    }

    override suspend fun getCurrentUser(): User? = MockData.currentUser

    override fun isLoggedIn(): Flow<Boolean> = isLoggedInState
}

// ─────────────────────────────────────────────
//  Course Repository Implementation (Mock)
// ─────────────────────────────────────────────
@Singleton
class CourseRepositoryImpl @Inject constructor() : CourseRepository {

    override suspend fun getCourses(): Result<List<Course>> {
        delay(800)
        return Result.success(MockData.courses)
    }

    override suspend fun getCourseById(id: String): Result<Course> {
        delay(400)
        val course = MockData.courses.find { it.id == id }
        return if (course != null) {
            Result.success(course)
        } else {
            Result.failure(Exception("Không tìm thấy khóa học"))
        }
    }

    override suspend fun getEnrolledCourses(): Result<List<Course>> {
        delay(600)
        return Result.success(MockData.courses.filter { it.progress > 0f })
    }

    override suspend fun getRecommendedCourses(): Result<List<Course>> {
        delay(600)
        return Result.success(MockData.courses.filter { it.progress == 0f })
    }

    override suspend fun searchCourses(query: String): Result<List<Course>> {
        delay(400)
        return Result.success(
            MockData.courses.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.instructor.contains(query, ignoreCase = true)
            },
        )
    }
}

// ─────────────────────────────────────────────
//  Lesson Repository Implementation (Mock)
// ─────────────────────────────────────────────
@Singleton
class LessonRepositoryImpl @Inject constructor() : LessonRepository {

    override suspend fun getLessonsForCourse(courseId: String): Result<List<Lesson>> {
        delay(500)
        return Result.success(MockData.lessonsForUIBasics.filter { it.courseId == courseId })
    }

    override suspend fun markLessonCompleted(lessonId: String): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
}

// ─────────────────────────────────────────────
//  User Repository Implementation (Real API)
// ─────────────────────────────────────────────
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: BitLearningApiService,
) : UserRepository {

    override suspend fun getUserProfile(): Result<User> = runCatching {
        val wrapper = api.getProfile()
        val dto = wrapper.data ?: error(wrapper.message ?: "Không thể tải hồ sơ")
        dto.toDomain()
    }

    override suspend fun updateProfile(user: User): Result<User> = runCatching {
        val wrapper = api.updateProfile(
            com.app.bitlearning.core.network.UpdateProfileBody(
                name = user.name,
                avatarUrl = user.avatar,
            ),
        )
        val dto = wrapper.data ?: error(wrapper.message ?: "Cập nhật thất bại")
        dto.toDomain()
    }

    override suspend fun getCertificates(): Result<List<Certificate>> = Result.success(emptyList()) // TODO: wire up /users/certificates when backend is ready
}
