/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import retrofit2.http.*

/**
 * Retrofit API service interface.
 * Currently not used (mock data in place), but ready to be wired into repositories
 * when the real API is available.
 *
 * Usage: inject this via Hilt into real repository implementations.
 */
interface BitLearningApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): TokenResponse

    @POST("auth/logout")
    suspend fun logout()

    // Courses
    @GET("courses")
    suspend fun getCourses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): CoursesResponse

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: String): CourseResponse

    @GET("courses/recommended")
    suspend fun getRecommendedCourses(): CoursesResponse

    @GET("users/me/courses")
    suspend fun getEnrolledCourses(): CoursesResponse

    // Lessons
    @GET("courses/{courseId}/lessons")
    suspend fun getLessons(@Path("courseId") courseId: String): LessonsResponse

    @POST("lessons/{lessonId}/complete")
    suspend fun markLessonCompleted(@Path("lessonId") lessonId: String)

    // User
    @GET("users/me")
    suspend fun getProfile(): UserResponse

    @PUT("users/me")
    suspend fun updateProfile(@Body body: UpdateProfileBody): UserResponse
}

// ─────────── Request Bodies ───────────
data class LoginBody(val email: String, val password: String)
data class RegisterBody(val name: String, val email: String, val password: String)
data class UpdateProfileBody(val name: String, val avatarUrl: String?)

// ─────────── Response DTOs ───────────
data class TokenResponse(val accessToken: String, val refreshToken: String, val expiresAt: Long)

data class CoursesResponse(val data: List<CourseDto>, val total: Int)
data class CourseResponse(val data: CourseDto)
data class LessonsResponse(val data: List<LessonDto>)
data class UserResponse(val data: UserDto)

data class CourseDto(
    val id: String,
    val title: String,
    val description: String,
    val instructor: String,
    val thumbnailUrl: String?,
    val category: String,
    val rating: Double,
    val reviewCount: Int,
    val duration: String,
    val lessonCount: Int,
    val progress: Float,
)

data class LessonDto(
    val id: String,
    val courseId: String,
    val order: Int,
    val title: String,
    val durationSeconds: Int,
    val videoUrl: String?,
    val isCompleted: Boolean,
    val isLocked: Boolean,
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val memberSince: Int,
    val isPremium: Boolean,
)
