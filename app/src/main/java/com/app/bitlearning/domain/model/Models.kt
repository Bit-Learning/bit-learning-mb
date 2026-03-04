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
    val id: String,
    val title: String,
    val description: String,
    val instructor: String,
    val thumbnailUrl: String?,
    val category: CourseCategory,
    val rating: Double,
    val reviewCount: Int,
    val duration: String, // e.g. "12h 30m"
    val lessonCount: Int,
    val progress: Float = 0f, // 0.0 → 1.0 (enrolled courses)
    val isLocked: Boolean = false,
)

// ─────────────────────────────────────────────
//  Lesson
// ─────────────────────────────────────────────
data class Lesson(
    val id: String,
    val courseId: String,
    val order: Int,
    val title: String,
    val durationSeconds: Int,
    val videoUrl: String?,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val isCurrentlyPlaying: Boolean = false,
)

// ─────────────────────────────────────────────
//  Certificate
// ─────────────────────────────────────────────
data class Certificate(
    val id: String,
    val courseId: String,
    val courseTitle: String,
    val issuedDate: String,
    val thumbnailUrl: String?,
)

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
