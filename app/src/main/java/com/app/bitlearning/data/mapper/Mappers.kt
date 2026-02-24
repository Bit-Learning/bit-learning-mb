/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.mapper

import com.app.bitlearning.core.network.*
import com.app.bitlearning.domain.model.*

/**
 * Extension functions to convert API DTOs → Domain Models.
 * Used by real repository implementations when API is live.
 */

fun CourseDto.toDomain(): Course = Course(
    id = id,
    title = title,
    description = description,
    instructor = instructor,
    thumbnailUrl = thumbnailUrl,
    category = CourseCategory.entries.find {
        it.name.equals(category, ignoreCase = true)
    } ?: CourseCategory.OTHER,
    rating = rating,
    reviewCount = reviewCount,
    duration = duration,
    lessonCount = lessonCount,
    progress = progress,
)

fun LessonDto.toDomain(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    order = order,
    title = title,
    durationSeconds = durationSeconds,
    videoUrl = videoUrl,
    isCompleted = isCompleted,
    isLocked = isLocked,
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    memberSince = memberSince,
    isPremium = isPremium,
)

fun TokenResponse.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresAt = expiresAt,
)
