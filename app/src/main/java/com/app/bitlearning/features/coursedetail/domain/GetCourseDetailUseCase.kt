/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.coursedetail.domain

import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.Section
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.LessonRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class CourseDetailData(
    val course: Course,
    val sections: List<Section>,
    val hasAccess: Boolean,
    val progress: Float,
)

class GetCourseDetailUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val lessonRepository: LessonRepository,
) {
    suspend operator fun invoke(courseId: Int): Result<CourseDetailData> = runCatching {
        coroutineScope {
            val courseDeferred = async { courseRepository.getCourseById(courseId).getOrThrow() }
            val sectionsDeferred = async { lessonRepository.getSectionsByCourse(courseId).getOrThrow() }
            val accessDeferred = async { courseRepository.checkCourseAccess(courseId).getOrElse { false } }
            val progressDeferred = async { courseRepository.getCourseProgress(courseId).getOrElse { 0f } }

            val sections = sectionsDeferred.await()
            val hasAccess = accessDeferred.await()
            val progress = progressDeferred.await()
            val course = courseDeferred.await().copy(
                sections = sections,
                progress = progress,
                hasAccess = hasAccess,
            )

            CourseDetailData(
                course = course,
                sections = sections,
                hasAccess = hasAccess,
                progress = progress,
            )
        }
    }
}
