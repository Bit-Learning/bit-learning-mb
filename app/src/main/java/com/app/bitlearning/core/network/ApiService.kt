/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.google.gson.annotations.SerializedName
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

    /** POST /auth/register → { status, message } — no token; user must activate via email */
    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): ApiWrapper<Unit>

    /** POST /auth/logout – Authorization header injected by [AuthInterceptor] */
    @POST("auth/logout")
    suspend fun logout()


    // ─── QR Web Login (Mobile as Authenticator) ────────────────────────────

    /**
     * Mobile confirms that a QR code displayed on the web app has been scanned.
     *
     * POST /auth/qr/scan { qrToken }
     * Requires an authenticated mobile user (Authorization header is added by
     * [AuthInterceptor]). Backend will update the Redis QR session to
     * SCANNED and push an SSE event to the web client.
     */
    @POST("auth/qr/scan")
    suspend fun scanQr(@Body body: QrTokenBody)

    /**
     * Mobile approves the web login for the given QR token.
     *
     * POST /auth/qr/confirm { qrToken }
     * Requires an authenticated mobile user. Backend will generate a JWT for
     * the web client and complete the QR login flow.
     */
    @POST("auth/qr/confirm")
    suspend fun confirmQr(@Body body: QrTokenBody)

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
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): ApiWrapper<List<CourseDto>>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: String): ApiWrapper<CourseDto>

    @GET("courses/recommended")
    suspend fun getRecommendedCourses(): ApiWrapper<List<CourseDto>>

    @GET("users/me/courses")
    suspend fun getEnrolledCourses(): ApiWrapper<List<CourseDto>>

    // ─── Lessons ────────────────────────────────────────────────────────────

    @GET("courses/{courseId}/lessons")
    suspend fun getLessons(@Path("courseId") courseId: String): ApiWrapper<List<LessonDto>>

    @POST("lessons/{lessonId}/complete")
    suspend fun markLessonCompleted(@Path("lessonId") lessonId: String)

    // ─── User ───────────────────────────────────────────────────────────────

    /** GET /users/profile – returns the currently authenticated user's profile */
    @GET("users/profile")
    suspend fun getProfile(): ApiWrapper<UserApiDto>

    @PUT("users/me")
    suspend fun updateProfile(@Body body: UpdateProfileBody): ApiWrapper<UserApiDto>
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
    val error: String? = null,
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

/** Simple body used by /auth/qr/scan and /auth/qr/confirm */
data class QrTokenBody(
    val qrToken: String,
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
