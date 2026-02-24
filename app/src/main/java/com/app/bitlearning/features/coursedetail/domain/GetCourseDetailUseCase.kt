/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.coursedetail.domain

import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.Lesson
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.LessonRepository
import javax.inject.Inject

data class CourseDetailData(
    val course: Course,
    val lessons: List<Lesson>,
)

class GetCourseDetailUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val lessonRepository: LessonRepository,
) {
    suspend operator fun invoke(courseId: String): Result<CourseDetailData> {
        val courseResult = courseRepository.getCourseById(courseId)
        val course = courseResult.getOrElse { return Result.failure(it) }
        val lessons = lessonRepository.getLessonsForCourse(courseId).getOrElse { emptyList() }
        return Result.success(CourseDetailData(course, lessons))
    }
}
