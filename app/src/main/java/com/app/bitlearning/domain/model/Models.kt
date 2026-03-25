/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.domain.model

// ─────────────────────────────────────────────
//  User
// ─────────────────────────────────────────────
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String,
    val memberSince: Int,
    val isPremium: Boolean,
)

// ─────────────────────────────────────────────
//  Course
// ─────────────────────────────────────────────
data class Course(
    val id: Int,
    val code: String = "",
    val title: String,
    val description: String,
    val instructor: String,
    val instructorId: Int? = null,
    val thumbnailUrl: String?,
    val category: CourseCategory = CourseCategory.OTHER,
    val level: CourseLevel = CourseLevel.BEGINNING,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val grade: Int? = null,
    val price: Int = 0,
    val totalSections: Int = 0,
    val totalLectures: Int = 0,
    val totalDuration: Int = 0,
    val progress: Float = 0f,
    val isLocked: Boolean = false,
    val sections: List<Section> = emptyList(),
)

// ─────────────────────────────────────────────
//  Section
// ─────────────────────────────────────────────
data class Section(
    val id: Int,
    val title: String,
    val description: String? = null,
    val orderIndex: Int = 0,
    val totalLectures: Int = 0,
    val totalDuration: Int = 0,
    val progressPercentage: Float = 0f,
    val lectures: List<Lecture> = emptyList(),
)

// ─────────────────────────────────────────────
//  Lecture (replaces Lesson)
// ─────────────────────────────────────────────
data class Lecture(
    val id: Int,
    val sectionId: Int? = null,
    val title: String,
    val description: String? = null,
    val type: LectureType = LectureType.VIDEO,
    val isPreviewable: Boolean = false,
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
)


// ─────────────────────────────────────────────
//  Certificate
// ─────────────────────────────────────────────
data class Certificate(
    val id: String,
    val courseId: Int,
    val courseTitle: String,
    val issuedDate: String,
    val thumbnailUrl: String?,
)

// ─────────────────────────────────────────────
//  Course Level Enum (matches backend)
// ─────────────────────────────────────────────
enum class CourseLevel(val displayName: String) {
    BEGINNING("Cơ bản"),
    INTERMEDIATE("Trung cấp"),
    ADVANCED("Nâng cao"),
}

// ─────────────────────────────────────────────
//  Lecture Type Enum (matches backend)
// ─────────────────────────────────────────────
enum class LectureType {
    VIDEO,
    TEXT,
    QUIZ,
}

// ─────────────────────────────────────────────
//  Category Enum
// ─────────────────────────────────────────────
enum class CourseCategory(val displayName: String, val colorHex: String) {
    DESIGN("Design", "#8B5CF6"),
    DEVELOPMENT("Development", "#0EA5E9"),
    BUSINESS("Business", "#F59E0B"),
    MARKETING("Marketing", "#EC4899"),
    DATA_SCIENCE("Data Science", "#10B981"),
    OTHER("Other", "#64748B"),
}

// ─────────────────────────────────────────────
//  Auth
// ─────────────────────────────────────────────
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)
