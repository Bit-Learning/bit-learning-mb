/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.mapper

import com.app.bitlearning.core.network.*
import com.app.bitlearning.domain.model.*

private fun Float?.toUnitProgress(): Float {
    val value = this ?: 0f
    return when {
        value.isNaN() -> 0f
        value <= 1f -> value.coerceIn(0f, 1f)
        else -> (value / 100f).coerceIn(0f, 1f)
    }
}

fun CoursePreviewDto.toDomain(): Course = Course(
    id = id,
    code = code.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    instructor = instructorName.orEmpty(),
    instructorId = instructorId,
    thumbnailUrl = thumbnailUrl,
    level = level?.let { lvl ->
        CourseLevel.entries.find { it.name.equals(lvl, ignoreCase = true) }
    } ?: CourseLevel.BEGINNING,
    rating = ratingStar ?: 0.0,
    reviewCount = ratingCount ?: 0,
    grade = grade,
    price = price ?: 0,
)

fun CourseDetailDto.toDomain(): Course = Course(
    id = id,
    code = code.orEmpty(),
    title = title.orEmpty(),
    subtitle = subtitle.orEmpty(),
    description = description.orEmpty(),
    instructor = instructorName.orEmpty(),
    instructorId = instructorId,
    thumbnailUrl = thumbnailUrl,
    level = level?.let { lvl ->
        CourseLevel.entries.find { it.name.equals(lvl, ignoreCase = true) }
    } ?: CourseLevel.BEGINNING,
    rating = ratingStar ?: 0.0,
    reviewCount = ratingCount ?: 0,
    grade = grade,
    price = price ?: 0,
    totalSections = totalSections ?: 0,
    totalLectures = totalLectures ?: 0,
    totalDuration = totalDuration ?: 0,
    progress = progressPercentage.toUnitProgress(),
    language = language.orEmpty(),
    outcome = outcome.orEmpty(),
    requirement = requirement.orEmpty(),
    audience = audience.orEmpty(),
    sections = sections?.map { it.toDomain() }?.filterNot { it.isDeleted } ?: emptyList(),
)

fun MyCourseDto.toDomain(): Course = Course(
    id = id,
    code = code.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    instructor = instructorName.orEmpty(),
    instructorId = instructorId,
    thumbnailUrl = thumbnailUrl,
    level = level?.let { lvl ->
        CourseLevel.entries.find { it.name.equals(lvl, ignoreCase = true) }
    } ?: CourseLevel.BEGINNING,
    rating = ratingStar ?: 0.0,
    reviewCount = ratingCount ?: 0,
    grade = grade,
    price = price ?: 0,
    progress = progressPercentage.toUnitProgress(),
)


fun EnrollmentDto.toDomain(): Course = Course(
    progress = progressPercentage.toUnitProgress(),
    id = courseId ?: 0,
    code = courseCode.orEmpty(),
    title = courseTitle.orEmpty(),
    description = "",
    instructor = instructorName.orEmpty(),
    instructorId = instructorId,
    thumbnailUrl = courseThumbnailUrl,
    level = level?.let { lvl ->
        CourseLevel.entries.find { it.name.equals(lvl, ignoreCase = true) }
    } ?: CourseLevel.BEGINNING,
    grade = grade,
    isCompleted = isCompleted ?: ((progressPercentage ?: 0f) >= 100f || progressPercentage.toUnitProgress() >= 1f),
)

fun SectionDetailDto.toDomain(): Section = Section(
    id = id ?: 0,
    title = title.orEmpty(),
    description = description,
    isPublished = isPublished ?: true,
    orderIndex = orderIndex ?: 0,
    totalLectures = totalLectures ?: 0,
    totalDuration = totalDuration ?: 0,
    progressPercentage = progressPercentage.toUnitProgress(),
    isDeleted = isDeleted ?: false,
    lectures = lectures?.map { it.toDomain() }?.filterNot { it.isDeleted } ?: emptyList(),
)

fun LectureDetailDto.toDomain(): Lecture = Lecture(
    id = id ?: 0,
    sectionId = sectionId,
    title = title.orEmpty(),
    description = description,
    type = type?.let { t ->
        LectureType.entries.find { it.name.equals(t, ignoreCase = true) }
    } ?: LectureType.VIDEO,
    isPreviewable = isPreviewable ?: false,
    orderIndex = orderIndex ?: 0,
    isCompleted = isCompleted ?: false,
    isDeleted = isDeleted ?: false,
    progress = progressPercentage.toUnitProgress(),
)

fun LectureTextDto.toDomain(): LectureTextContent = LectureTextContent(
    lecture = lecture?.toDomain() ?: Lecture(id = 0, title = ""),
    content = content.orEmpty(),
)

fun AnswerDetailDto.toDomain(): QuizAnswer = QuizAnswer(
    id = id ?: 0,
    answerText = answerText.orEmpty(),
    isCorrect = isCorrect ?: false,
    orderIndex = orderIndex ?: 0,
)

fun QuizDetailDto.toDomain(): QuizQuestion = QuizQuestion(
    id = id ?: 0,
    questionText = questionText.orEmpty(),
    orderIndex = orderIndex ?: 0,
    answers = answers?.map { it.toDomain() }?.sortedBy { it.orderIndex } ?: emptyList(),
)

fun LectureQuizDto.toDomain(): LectureQuizContent = LectureQuizContent(
    lecture = lecture?.toDomain() ?: Lecture(id = 0, title = ""),
    passPercent = passPercent ?: 0.7f,
    maxAttempts = maxAttempts ?: 1,
    quizzes = quizzes?.map { it.toDomain() }?.sortedBy { it.orderIndex } ?: emptyList(),
)

fun UserApiDto.toDomain(): User = User(
    id = id?.toString() ?: "",
    name = listOfNotNull(firstName?.trim(), lastName?.trim())
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { username ?: email ?: "" },
    email = email ?: "",
    avatar = avatar ?: "https://randomuser.me/api/portraits/lego/1.jpg",
    memberSince = createdAt?.take(4)?.toIntOrNull() ?: 2024,
    isPremium = role?.equals("PREMIUM", ignoreCase = true) ?: false,
)
