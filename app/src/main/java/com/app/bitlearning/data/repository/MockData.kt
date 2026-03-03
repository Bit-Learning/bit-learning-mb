/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.repository

import com.app.bitlearning.domain.model.*

/**
 * Mock data used in place of real API responses.
 * When API is ready, replace these with actual network calls.
 */
object MockData {

    val currentUser = User(
        id = "user_001",
        name = "Alex Harrison",
        email = "user@gmail.com",
        avatarUrl = "https://randomuser.me/api/portraits/men/32.jpg",
        memberSince = 2022,
        isPremium = true,
    )

    val courses = listOf(
        Course(
            id = "c001",
            title = "Hệ thống thiết kế UI nâng cao",
            description = "Nắm vững các nguyên tắc thiết kế UI/UX hiện đại. Từ design system, color theory đến auto-layout và prototyping.",
            instructor = "Jane Doe",
            thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600",
            category = CourseCategory.DESIGN,
            rating = 4.9,
            reviewCount = 1200,
            duration = "14h 20m",
            lessonCount = 32,
            progress = 0.65f,
        ),
        Course(
            id = "c002",
            title = "Python cho Khoa học dữ liệu",
            description = "Học Python từ cơ bản đến nâng cao, ứng dụng trong phân tích dữ liệu, machine learning và visualization.",
            instructor = "Dr. Angela Smith",
            thumbnailUrl = "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=600",
            category = CourseCategory.DATA_SCIENCE,
            rating = 4.8,
            reviewCount = 1200,
            duration = "20h 00m",
            lessonCount = 48,
        ),
        Course(
            id = "c003",
            title = "Làm chủ Typography",
            description = "Từ font selection đến typographic hierarchy và responsive text, mọi thứ bạn cần biết về typography trong thiết kế.",
            instructor = "Marcus Aurelius",
            thumbnailUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
            category = CourseCategory.DESIGN,
            rating = 4.9,
            reviewCount = 850,
            duration = "8h 15m",
            lessonCount = 20,
        ),
        Course(
            id = "c004",
            title = "Khóa học Full-Stack",
            description = "Trở thành Full Stack Developer với React, Node.js, PostgreSQL và Docker. Xây dựng ứng dụng production-ready.",
            instructor = "Dev Mastery",
            thumbnailUrl = "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=600",
            category = CourseCategory.DEVELOPMENT,
            rating = 4.7,
            reviewCount = 2400,
            duration = "36h 00m",
            lessonCount = 84,
        ),
        Course(
            id = "c005",
            title = "Marketing kỹ thuật số cơ bản",
            description = "SEO, Social Media Marketing, Email Marketing và Google Ads. Chiến lược marketing tổng thể cho doanh nghiệp.",
            instructor = "Sarah Jenkins",
            thumbnailUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600",
            category = CourseCategory.MARKETING,
            rating = 4.6,
            reviewCount = 3100,
            duration = "10h 30m",
            lessonCount = 25,
        ),
        Course(
            id = "c006",
            title = "Cơ bản về thiết kế UI",
            description = "Khóa học cơ bản về UI Design, bao gồm Figma, wireframing, prototyping và design handoff.",
            instructor = "Sarah Jenkins",
            thumbnailUrl = "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?w=600",
            category = CourseCategory.DESIGN,
            rating = 4.7,
            reviewCount = 980,
            duration = "12h 45m",
            lessonCount = 28,
            progress = 0.45f,
        ),
    )

    val lessonsForUIBasics = listOf(
        Lesson(
            id = "l001",
            courseId = "c006",
            order = 1,
            title = "Giới thiệu về thiết kế UI",
            durationSeconds = 504,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            isCompleted = true,
        ),
        Lesson(
            id = "l002",
            courseId = "c006",
            order = 2,
            title = "Nguyên tắc & Mô hình thiết kế",
            durationSeconds = 765,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            isCurrentlyPlaying = true,
        ),
        Lesson(
            id = "l003",
            courseId = "c006",
            order = 3,
            title = "Làm chủ lý thuyết màu sắc",
            durationSeconds = 910,
            videoUrl = null,
        ),
        Lesson(
            id = "l004",
            courseId = "c006",
            order = 4,
            title = "Typography và Hệ thống lưới",
            durationSeconds = 1325,
            videoUrl = null,
        ),
        Lesson(
            id = "l005",
            courseId = "c006",
            order = 5,
            title = "Làm chủ Auto-Layout",
            durationSeconds = 1110,
            videoUrl = null,
            isLocked = true,
        ),
    )

    val certificates = listOf(
        Certificate(
            id = "cert_001",
            courseId = "c002",
            courseTitle = "Python cho Khoa học dữ liệu",
            issuedDate = "15/01/2024",
            thumbnailUrl = "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=300",
        ),
        Certificate(
            id = "cert_002",
            courseId = "c001",
            courseTitle = "Hệ thống thiết kế UI nâng cao",
            issuedDate = "20/10/2023",
            thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=300",
        ),
        Certificate(
            id = "cert_003",
            courseId = "c004",
            courseTitle = "Khóa học Full-Stack",
            issuedDate = "15/09/2023",
            thumbnailUrl = "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=300",
        ),
        Certificate(
            id = "cert_004",
            courseId = "c005",
            courseTitle = "Marketing kỹ thuật số cơ bản",
            issuedDate = "02/08/2023",
            thumbnailUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=300",
        ),
    )
}
