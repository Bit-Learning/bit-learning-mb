/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.mapper

import com.app.bitlearning.core.network.*
import com.app.bitlearning.domain.model.*

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
    progress = progressPercentage ?: 0f,
    sections = sections?.map { it.toDomain() } ?: emptyList(),
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
    progress = progressPercentage ?: 0f,
)


fun EnrollmentDto.toDomain(): Course = Course(
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
    progress = progressPercentage ?: 0f,
)

fun SectionDetailDto.toDomain(): Section = Section(
    id = id ?: 0,
    title = title.orEmpty(),
    description = description,
    orderIndex = orderIndex ?: 0,
    totalLectures = totalLectures ?: 0,
    totalDuration = totalDuration ?: 0,
    progressPercentage = progressPercentage ?: 0f,
    lectures = lectures?.map { it.toDomain() } ?: emptyList(),
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
