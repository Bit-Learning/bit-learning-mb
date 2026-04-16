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
import okhttp3.MediaType.Companion.toMediaType

// ─────────────────────────────────────────────
//  Auth Repository Implementation (Mock)
// ─────────────────────────────────────────────
@Singleton
class MockAuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val isLoggedInState = MutableStateFlow(false)

    override suspend fun login(request: LoginRequest): Result<AuthToken> {
        delay(1200)
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

    override suspend fun logout() {
        isLoggedInState.value = false
    }

    override suspend fun getCurrentUser(): User? = null

    override fun isLoggedIn(): Flow<Boolean> = isLoggedInState
}


// ─────────────────────────────────────────────
//  Course Repository Implementation (Real API)
// ─────────────────────────────────────────────
@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val api: BitLearningApiService,
) : CourseRepository {

    override suspend fun getCourses(page: Int, size: Int): Result<List<Course>> = runCatching {
        val wrapper = api.getCourses(page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getCourseById(id: Int): Result<Course> = runCatching {
        val wrapper = api.getCourseById(id)
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Không tìm thấy khóa học")
    }

    override suspend fun getCoursesByGrade(grade: Int, page: Int, size: Int): Result<List<Course>> = runCatching {
        val wrapper = api.getCoursesByGrade(grade = grade, page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getEnrolledCourses(page: Int, size: Int): Result<List<Course>> = runCatching {
        val wrapper = api.getEnrolledCourses(page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getMyCourses(page: Int, size: Int): Result<List<Course>> = runCatching {
        val wrapper = api.getMyCourses(page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun searchCourses(query: String): Result<List<Course>> = runCatching {
        // Backend doesn't have a dedicated search endpoint yet; filter from all courses
        val wrapper = api.getCourses(page = 0, size = 50)
        wrapper.data?.map { it.toDomain() }?.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.instructor.contains(query, ignoreCase = true)
        } ?: emptyList()
    }
}

// ─────────────────────────────────────────────
//  Lesson Repository Implementation (Real API)
// ─────────────────────────────────────────────
@Singleton
class LessonRepositoryImpl @Inject constructor(
    private val api: BitLearningApiService,
) : LessonRepository {

    override suspend fun getLessonsForCourse(courseId: Int): Result<List<Lecture>> = runCatching {
        // Get course detail which includes sections → lectures
        val wrapper = api.getCourseById(courseId)
        val detail = wrapper.data ?: error(wrapper.message ?: "Không tìm thấy khóa học")
        detail.sections?.flatMap { section ->
            section.lectures?.map { it.toDomain() } ?: emptyList()
        } ?: emptyList()
    }

    override suspend fun markLectureCompleted(lectureId: Int): Result<Unit> = runCatching {
        api.markLectureCompleted(lectureId)
        Unit
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
        // Split name into firstName/lastName for backend
        val parts = user.name.trim().split("\\s+".toRegex(), limit = 2)
        val wrapper = api.updateProfile(
            com.app.bitlearning.core.network.UpdateProfileBody(
                firstName = parts.getOrNull(0),
                lastName = parts.getOrNull(1),
                bio = user.bio,
                phoneNumber = user.phone,
                location = user.location,
                jobTitle = user.jobTitle,
                pronouns = user.pronouns,
            ),
        )
        val dto = wrapper.data ?: error(wrapper.message ?: "Cập nhật thất bại")
        dto.toDomain()
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<String> = runCatching {
        // Get current user ID first
        val profileWrapper = api.getProfile()
        val userId = profileWrapper.data?.id ?: error("Không thể lấy thông tin người dùng")

        val requestBody = okhttp3.RequestBody.Companion.create(
            "image/*".toMediaType(), imageBytes,
        )
        val part = okhttp3.MultipartBody.Part.createFormData("avatar", fileName, requestBody)
        val wrapper = api.uploadAvatar(userId, part)
        wrapper.data ?: error(wrapper.message ?: "Upload thất bại")
    }

    override suspend fun uploadCover(imageBytes: ByteArray, fileName: String): Result<String> = runCatching {
        // Get current user ID first
        val profileWrapper = api.getProfile()
        val userId = profileWrapper.data?.id ?: error("Không thể lấy thông tin người dùng")

        val requestBody = okhttp3.RequestBody.Companion.create(
            "image/*".toMediaType(), imageBytes,
        )
        val part = okhttp3.MultipartBody.Part.createFormData("cover", fileName, requestBody)
        val wrapper = api.uploadCover(userId, part)
        wrapper.data ?: error(wrapper.message ?: "Upload thất bại")
    }

    override suspend fun getCertificates(): Result<List<Certificate>> = Result.success(emptyList())
}

// ─────────────────────────────────────────────
//  Onboarding Repository Implementation (Real API)
// ─────────────────────────────────────────────
@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val api: BitLearningApiService,
) : OnboardingRepository {

    override suspend fun getOnboardingPages(): Result<List<OnboardingPage>> = runCatching {
        val wrapper = api.getOnboardingPages()
        wrapper.data?.map { it.toDomain() }?.sortedBy { it.orderIndex } ?: emptyList()
    }
}

// ─────────────────────────────────────────────
//  Forum Repository Implementation (Real API)
// ─────────────────────────────────────────────
@Singleton
class ForumRepositoryImpl @Inject constructor(
    private val api: BitLearningApiService,
) : ForumRepository {

    override suspend fun getPosts(page: Int, size: Int): Result<List<ForumPost>> = runCatching {
        val wrapper = api.getForumPosts(page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getPostById(id: Int): Result<ForumPost> = runCatching {
        val wrapper = api.getForumPostById(id)
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Không tìm thấy bài viết")
    }

    override suspend fun createPost(title: String, content: String, tags: List<String>): Result<ForumPost> = runCatching {
        val wrapper = api.createForumPost(
            com.app.bitlearning.core.network.CreateForumPostBody(title = title, content = content, tags = tags),
        )
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Tạo bài viết thất bại")
    }

    override suspend fun getComments(postId: Int, page: Int, size: Int): Result<List<ForumComment>> = runCatching {
        val wrapper = api.getForumComments(postId = postId, page = page, size = size)
        wrapper.data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun createComment(postId: Int, content: String): Result<ForumComment> = runCatching {
        val wrapper = api.createForumComment(
            postId = postId,
            body = com.app.bitlearning.core.network.CreateForumCommentBody(content = content),
        )
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Gửi bình luận thất bại")
    }

    override suspend fun likePost(postId: Int): Result<Unit> = runCatching {
        api.likeForumPost(postId)
        Unit
    }

    override suspend fun unlikePost(postId: Int): Result<Unit> = runCatching {
        api.unlikeForumPost(postId)
        Unit
    }
}
