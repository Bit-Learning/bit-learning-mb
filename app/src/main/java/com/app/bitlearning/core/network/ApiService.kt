/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Streaming
import retrofit2.http.*

/**
 * Retrofit API service interface wired to the real Bit Learning backend.
 *
 * Base URL: http://10.0.0.2:8082/api/
 *
 * All responses are wrapped in [ApiWrapper] which mirrors the backend's
 * `ApiResponse<T>` envelope: `{ status, message, data?, error? }`.
 */
interface BitLearningApiService {

    // ─── Auth ───────────────────────────────────────────────────────────────

    /** POST /auth/login → { status, message, data: { accessToken, user } } */
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): ApiWrapper<LoginApiResponse>

    /** POST /auth/refresh-token → { status, message, data: { accessToken, user } } */
    @POST("auth/refresh-token")
    suspend fun refreshToken(): ApiWrapper<LoginApiResponse>

    /** POST /auth/register → { status, message } — no token; user must activate via email */
    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): ApiWrapper<Unit>

    /** POST /auth/logout – Authorization header injected by [AuthInterceptor] */
    @POST("auth/logout")
    suspend fun logout()

    // ─── OAuth2 ─────────────────────────────────────────────────────────────

    /**
     * Exchange Google authorization-code for JWT tokens.
     * POST /auth/oauth2/google
     *
     * Note: The backend's `oauth2.google.redirect-uri` property **must** match
     * the [redirectUri] value sent here (= [OAUTH2_REDIRECT_URI]).
     * Register the same URI as an Authorized Redirect URI in your Google Cloud
     * Console OAuth2 app.
     */
    @POST("auth/oauth2/google")
    suspend fun loginWithGoogle(@Body body: OAuth2LoginBody): ApiWrapper<OAuth2ApiResponse>

    /**
     * Exchange GitHub authorization-code for JWT tokens.
     * POST /auth/oauth2/github
     *
     * Note: `oauth2.github.redirect-uri` on the backend must equal [redirectUri]
     * and be registered in the GitHub OAuth App settings.
     */
    @POST("auth/oauth2/github")
    suspend fun loginWithGitHub(@Body body: OAuth2LoginBody): ApiWrapper<OAuth2ApiResponse>

    /**
     * Verify a Google ID Token from the Android Google Sign-In SDK.
     * POST /auth/oauth2/google/mobile
     */
    @POST("auth/oauth2/google/mobile")
    suspend fun loginWithGoogleIdToken(@Body body: GoogleIdTokenBody): ApiWrapper<OAuth2ApiResponse>

    // ─── Courses ────────────────────────────────────────────────────────────

    @GET("courses")
    suspend fun getCourses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
    ): ApiWrapper<List<CoursePreviewDto>>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: Int): ApiWrapper<CourseDetailDto>

    @GET("courses/grade/{grade}")
    suspend fun getCoursesByGrade(
        @Path("grade") grade: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
    ): ApiWrapper<List<CoursePreviewDto>>

    @GET("courses/my-courses")
    suspend fun getMyCourses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
    ): ApiWrapper<List<MyCourseDto>>

    // ─── Enrollment ─────────────────────────────────────────────────────────

    @GET("enrollments/my-courses")
    suspend fun getEnrolledCourses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
    ): ApiWrapper<List<EnrollmentDto>>

    @POST("enrollments/enroll/{courseId}")
    suspend fun enrollCourse(@Path("courseId") courseId: Int): ApiWrapper<Unit>

    @GET("enrollments/courses/{courseId}/access")
    suspend fun checkCourseAccess(@Path("courseId") courseId: Int): ApiWrapper<Boolean>

    @GET("enrollments/courses/{courseId}/progress")
    suspend fun getCourseProgress(@Path("courseId") courseId: Int): ApiWrapper<Float>

    // ─── Learning ───────────────────────────────────────────────────────────

    @GET("sections")
    suspend fun getSectionsByCourse(
        @Query("courseId") courseId: Int,
    ): ApiWrapper<List<SectionDetailDto>>

    @GET("lectures/lecture-quizzes/{lectureId}")
    suspend fun getLectureQuiz(@Path("lectureId") lectureId: Int): ApiWrapper<LectureQuizDto>

    @GET("lectures/lecture-texts/{lectureId}")
    suspend fun getLectureText(@Path("lectureId") lectureId: Int): ApiWrapper<LectureTextDto>

    @GET("learning/progress/lectures/{lectureId}")
    suspend fun getLectureProgress(@Path("lectureId") lectureId: Int): ApiWrapper<Int>

    @GET("learning/progress/lectures/{lectureId}/is-completed")
    suspend fun isLectureCompleted(@Path("lectureId") lectureId: Int): ApiWrapper<Boolean>

    @POST("learning/progress/sync")
    suspend fun syncProgress(@Body body: SyncProgressBody): ApiWrapper<Unit>

    @POST("learning/progress/lectures/{lectureId}/complete")
    suspend fun markLectureCompleted(@Path("lectureId") lectureId: Int): ApiWrapper<Unit>

    @Streaming
    @GET("courses/{courseId}/certificate")
    suspend fun getCertificate(@Path("courseId") courseId: Int): Response<ResponseBody>

    // ─── User ───────────────────────────────────────────────────────────────

    /** GET /users/profile – returns the currently authenticated user's profile */
    @GET("users/profile")
    suspend fun getProfile(): ApiWrapper<UserApiDto>

    @PUT("users/me")
    suspend fun updateProfile(@Body body: UpdateProfileBody): ApiWrapper<UserApiDto>
}

interface BitLearningRefreshApiService {

    @POST("auth/refresh-token")
    fun refreshToken(): retrofit2.Call<ApiWrapper<LoginApiResponse>>
}

// ─────────────────────────────────────────────────────────────────────────────
//  Generic API envelope
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Mirrors the backend's `ApiResponse<T>` structure.
 * Successful responses carry data in [data]; error responses carry a message in [error].
 */
data class ApiWrapper<T>(
    val status: Int = 0,
    val message: String? = null,
    val data: T? = null,
    val page: PageInfoDto? = null,
    val error: String? = null,
)

data class PageInfoDto(
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
)

// ─────────────────────────────────────────────────────────────────────────────
//  Request bodies
// ─────────────────────────────────────────────────────────────────────────────

data class LoginBody(
    val email: String,
    val password: String,
)

/**
 * Backend's RegisterRequest uses separate firstName / lastName fields.
 * The UI collects a single "full name" field and splits it before calling the API.
 */
data class RegisterBody(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)

data class UpdateProfileBody(
    val name: String,
    val avatarUrl: String?,
)

/**
 * Sent to POST /auth/oauth2/google/mobile (Google Sign-In SDK flow).
 * The [idToken] is obtained from [GoogleSignInAccount.getIdToken()].
 */
data class GoogleIdTokenBody(
    val idToken: String,
)

/**
 * Sent to /auth/oauth2/google and /auth/oauth2/github (web OAuth2 code flow).
 */
data class OAuth2LoginBody(
    val code: String,
    val redirectUri: String,
)

data class SyncProgressBody(
    val lectureId: Int,
    val currentSecond: Int,
    val totalDuration: Int,
)

// ─────────────────────────────────────────────────────────────────────────────
//  Response DTOs
// ─────────────────────────────────────────────────────────────────────────────

/** data payload inside ApiWrapper for /auth/login */
data class LoginApiResponse(
    val accessToken: String?,
    val user: UserApiDto?,
)

/** data payload inside ApiWrapper for /auth/oauth2/google|github */
data class OAuth2ApiResponse(
    val accessToken: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    val user: OAuth2UserDto?,
    val isNewUser: Boolean = false,
    val message: String? = null,
)

data class OAuth2UserDto(
    val id: Int?,
    val email: String?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?,
    val role: String?,
    val provider: String?,
)

/** Matches the backend's UserResponse DTO */
data class UserApiDto(
    val id: Int?,
    val username: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?,
    val role: String?,
    val activated: Boolean = false,
    val createdAt: String? = null,
)

// ─── Course DTOs (matching backend CourseDto.java) ──────────────────────

data class CoursePreviewDto(
    val id: Int,
    val code: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val instructorId: Int?,
    val instructorName: String?,
    val ratingStar: Double?,
    val ratingCount: Int?,
    val level: String?,
    val grade: Int?,
    val price: Int?,
    val isDeleted: Boolean?,
    val status: String?,
)

data class CourseDetailDto(
    val id: Int,
    val code: String?,
    val title: String?,
    val instructorId: Int?,
    val instructorName: String?,
    val subtitle: String?,
    val thumbnailUrl: String?,
    val language: String?,
    val outcome: String?,
    val requirement: String?,
    val audience: String?,
    val level: String?,
    val description: String?,
    val ratingStar: Double?,
    val ratingCount: Int?,
    val totalSections: Int?,
    val totalLectures: Int?,
    val totalDuration: Int?,
    val grade: Int?,
    val price: Int?,
    val sections: List<SectionDetailDto>?,
    val isDeleted: Boolean?,
    val status: String?,
    val progressPercentage: Float?,
)

data class MyCourseDto(
    val id: Int,
    val code: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val instructorId: Int?,
    val instructorName: String?,
    val ratingStar: Double?,
    val ratingCount: Int?,
    val level: String?,
    val grade: Int?,
    val price: Int?,
    val isDeleted: Boolean?,
    val status: String?,
    val progressPercentage: Float?,
)

data class SectionDetailDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val isPublished: Boolean?,
    val orderIndex: Int?,
    val totalLectures: Int?,
    val totalDuration: Int?,
    val isDeleted: Boolean?,
    val lectures: List<LectureDetailDto>?,
    val progressPercentage: Float?,
)

data class LectureDetailDto(
    val id: Int?,
    val sectionId: Int?,
    val title: String?,
    val description: String?,
    val type: String?,
    val isPreviewable: Boolean?,
    val orderIndex: Int?,
    val isDeleted: Boolean?,
    val isCompleted: Boolean?,
    val progressPercentage: Float? = null,
)

data class LectureTextDto(
    val lecture: LectureDetailDto?,
    val content: String?,
)

data class AnswerDetailDto(
    val id: Int?,
    val answerText: String?,
    val isCorrect: Boolean?,
    val orderIndex: Int?,
)

data class QuizDetailDto(
    val id: Int?,
    val questionText: String?,
    val orderIndex: Int?,
    val answers: List<AnswerDetailDto>?,
)

data class LectureQuizDto(
    val lecture: LectureDetailDto?,
    val passPercent: Float?,
    val maxAttempts: Int?,
    val quizzes: List<QuizDetailDto>?,
)

data class EnrollmentDto(
    val enrollmentId: Int?,
    val progressPercentage: Float?,
    val isCompleted: Boolean?,
    val courseId: Int?,
    val courseCode: String?,
    val courseTitle: String?,
    val courseThumbnailUrl: String?,
    val instructorName: String?,
    val instructorId: Int?,
    val level: String?,
    val grade: Int?,
    val createdAt: String?,
)
