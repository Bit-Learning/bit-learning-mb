/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.repository

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import com.app.bitlearning.core.network.NetworkModule
import com.app.bitlearning.core.network.BitLearningApiService
import com.app.bitlearning.core.network.SyncProgressBody
import com.app.bitlearning.data.mapper.toDomain
import com.app.bitlearning.domain.model.*
import com.app.bitlearning.domain.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException
import timber.log.Timber

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
    @ApplicationContext private val context: Context,
) : CourseRepository {

    override suspend fun getCourses(page: Int, size: Int): Result<List<Course>> = runCatching {
        val wrapper = api.getCourses(page = page, size = size)
        Timber.tag("CourseRepositoryImpl").d("Fetched courses: ${wrapper.data?.size ?: 0}")
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
        wrapper.data
            ?.filterNot { it.isDeleted == true }
            ?.map { it.toDomain() }
            ?: emptyList()
    }

    override suspend fun checkCourseAccess(courseId: Int): Result<Boolean> = runCatching {
        val wrapper = api.checkCourseAccess(courseId)
        wrapper.data ?: false
    }

    override suspend fun getCourseProgress(courseId: Int): Result<Float> = runCatching {
        val wrapper = api.getCourseProgress(courseId)
        normalizeProgress(wrapper.data)
    }

    override suspend fun getCertificate(courseId: Int, courseTitle: String): Result<Certificate> = runCatching {
        val response = api.getCertificate(courseId)
        if (!response.isSuccessful) {
            error("Không thể tải chứng chỉ")
        }

        val issuedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val file = saveCertificateToCache(
            context = context,
            courseId = courseId,
            courseTitle = courseTitle,
            bytes = response.body()?.bytes() ?: error("Chứng chỉ trống"),
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )

        Certificate(
            id = "course-$courseId",
            courseId = courseId,
            courseTitle = courseTitle,
            issuedDate = issuedDate,
            thumbnailUrl = uri.toString(),
            localUri = uri.toString(),
        )
    }

    override suspend fun searchCourses(query: String): Result<List<Course>> = runCatching {
        val wrapper = api.getCourses(page = 0, size = 100)
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

    override suspend fun getSectionsByCourse(courseId: Int): Result<List<Section>> = runCatching {
        val wrapper = api.getSectionsByCourse(courseId)
        wrapper.data?.map { it.toDomain() }?.filterNot { it.isDeleted } ?: emptyList()
    }

    override suspend fun getLessonsForCourse(courseId: Int): Result<List<Lecture>> = runCatching {
        getSectionsByCourse(courseId)
            .getOrThrow()
            .flatMap { section -> section.lectures }
            .sortedBy { it.orderIndex }
    }

    override suspend fun getVideoM3u8Url(lectureId: Int): Result<String> = runCatching {
        "${NetworkModule.BASE_URL}lectures/lecture-videos/$lectureId/m3u8"
    }

    override suspend fun getLectureText(lectureId: Int): Result<LectureTextContent> = runCatching {
        val wrapper = api.getLectureText(lectureId)
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Không tải được nội dung bài học")
    }

    override suspend fun getLectureQuiz(lectureId: Int): Result<LectureQuizContent> = runCatching {
        val wrapper = api.getLectureQuiz(lectureId)
        wrapper.data?.toDomain() ?: error(wrapper.message ?: "Không tải được bài tập")
    }

    override suspend fun getLectureProgress(lectureId: Int): Result<Int> = runCatching {
        val wrapper = api.getLectureProgress(lectureId)
        wrapper.data ?: 0
    }

    override suspend fun isLectureCompleted(lectureId: Int): Result<Boolean> = runCatching {
        val wrapper = api.isLectureCompleted(lectureId)
        wrapper.data ?: false
    }

    override suspend fun syncProgress(request: SyncProgressRequest): Result<Unit> = runCatching {
        api.syncProgress(
            SyncProgressBody(
                lectureId = request.lectureId,
                currentSecond = request.currentSecond,
                totalDuration = request.totalDuration,
            ),
        )
        Unit
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
    private val courseRepository: CourseRepository,
) : UserRepository {

    override suspend fun getUserProfile(): Result<User> = runCatching {
        val wrapper = api.getProfile()
        Timber.d("Fetched user profile: ${wrapper.data ?: "null"}")
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

    override suspend fun getCertificates(): Result<List<Certificate>> = runCatching {
        val courses = api.getEnrolledCourses(page = 0, size = 100).data.orEmpty()
            .map { it.toDomain() }
            .filter { it.isCompleted || it.progress >= 1f }

        courses.map { course ->
            courseRepository.getCertificate(course.id, course.title).getOrThrow()
        }
    }
}

private fun normalizeProgress(value: Float?): Float {
    val raw = value ?: 0f
    return if (raw <= 1f) raw.coerceIn(0f, 1f) else (raw / 100f).coerceIn(0f, 1f)
}

private fun saveCertificateToCache(
    context: Context,
    courseId: Int,
    courseTitle: String,
    bytes: ByteArray,
): File {
    val safeTitle = courseTitle.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
    val dir = File(context.cacheDir, "certificates").apply { mkdirs() }
    val file = File(dir, "certificate-$courseId-$safeTitle.png")
    file.writeBytes(bytes)
    return file
}

internal fun Throwable.toUserMessage(default: String): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "Phiên đăng nhập đã hết hạn"
        403 -> "Bạn chưa có quyền truy cập dữ liệu này"
        404 -> "Không tìm thấy dữ liệu"
        else -> message ?: default
    }
    else -> message ?: default
}
