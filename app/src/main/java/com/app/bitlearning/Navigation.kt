/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.bitlearning.features.auth.ui.AuthScreen
import com.app.bitlearning.features.coursedetail.ui.CourseDetailScreen
import com.app.bitlearning.features.courses.ui.CoursesScreen
import com.app.bitlearning.features.home.ui.HomeScreen
import com.app.bitlearning.features.notification.ui.NotificationScreen
import com.app.bitlearning.features.player.ui.PlayerScreen
import com.app.bitlearning.features.profile.ui.EditProfileScreen
import com.app.bitlearning.features.profile.ui.MyCertificatesScreen
import com.app.bitlearning.features.profile.ui.ProfileScreen
import com.app.bitlearning.features.search.ui.SearchScreen
import com.app.bitlearning.features.splash.ui.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val HOME = "home"
    const val SEARCH = "search"
    const val COURSES = "courses"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val MY_CERTIFICATES = "my_certificates"
    const val NOTIFICATION = "notification"
    const val COURSE_DETAIL = "course_detail/{courseId}"
    const val PLAYER = "player/{courseId}"

    fun courseDetail(courseId: String) = "course_detail/$courseId"
    fun player(courseId: String) = "player/$courseId"
}

@Composable
fun BitLearningNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Routes.courseDetail(courseId))
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToCourses = {
                    navController.navigate(Routes.COURSES)
                },
                onNavigateToSearch = {
                    navController.navigate(Routes.SEARCH)
                },
                onNavigateToNotification = {
                    navController.navigate(Routes.NOTIFICATION)
                },
            )
        }

        composable(Routes.COURSES) {
            CoursesScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Routes.courseDetail(courseId))
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToSearch = {
                    navController.navigate(Routes.SEARCH)
                },
                onNavigateToNotification = {
                    navController.navigate(Routes.NOTIFICATION)
                },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Routes.courseDetail(courseId))
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME)
                },
                onNavigateToCourses = {
                    navController.navigate(Routes.COURSES)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.EDIT_PROFILE) },
                onNavigateToCertificates = { navController.navigate(Routes.MY_CERTIFICATES) },
            )
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Routes.MY_CERTIFICATES) {
            MyCertificatesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
            )
        }

        composable(Routes.NOTIFICATION) {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
            )
        }

        composable(Routes.COURSE_DETAIL) { backStack ->
            val courseId = backStack.arguments?.getString("courseId") ?: return@composable
            CourseDetailScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onStartLesson = { lessonCourseId ->
                    navController.navigate(Routes.player(lessonCourseId))
                },
            )
        }

        composable(Routes.PLAYER) { backStack ->
            val courseId = backStack.arguments?.getString("courseId") ?: return@composable
            PlayerScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
