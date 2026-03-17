/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.bitlearning.features.auth.ui.AuthScreen
import com.app.bitlearning.features.coursedetail.ui.CourseDetailScreen
import com.app.bitlearning.features.courses.ui.CoursesScreen
import com.app.bitlearning.features.history.ui.LearningHistoryScreen
import com.app.bitlearning.features.home.ui.HomeScreen
import com.app.bitlearning.features.notification.ui.NotificationScreen
import com.app.bitlearning.features.notificationsetting.ui.NotificationSettingScreen
import com.app.bitlearning.features.payment.ui.PaymentSettingScreen
import com.app.bitlearning.features.player.ui.PlayerScreen
import com.app.bitlearning.features.profile.ui.EditProfileScreen
import com.app.bitlearning.features.profile.ui.MyCertificatesScreen
import com.app.bitlearning.features.profile.ui.ProfileScreen
import com.app.bitlearning.features.search.ui.SearchScreen
import com.app.bitlearning.features.splash.ui.SplashScreen
import com.app.bitlearning.features.support.ui.SupportScreen

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
    const val LEARNING_HISTORY = "learning_history"
    const val SUPPORT = "support"
    const val NOTIFICATION_SETTING = "notification_setting"
    const val PAYMENT_SETTING = "payment_setting"
    const val COURSE_DETAIL = "course_detail/{courseId}"
    const val PLAYER = "player/{courseId}"

    fun courseDetail(courseId: String) = "course_detail/$courseId"
    fun player(courseId: String) = "player/$courseId"
}

@Composable
fun BitLearningNavGraph(
    navController: NavHostController,
    deepLinkUri: Uri? = null,
) {
    // Handle incoming App Link deep link URI.
    // Fires whenever deepLinkUri changes (cold-start or onNewIntent hot-start).
    // Navigation happens after the SplashScreen resolves auth state, so we
    // store the intent and navigate from here once the graph is ready.
    LaunchedEffect(deepLinkUri) {
        deepLinkUri ?: return@LaunchedEffect
        val screen = deepLinkUri.getQueryParameter("screen")
        val id = deepLinkUri.getQueryParameter("id")
        when (screen) {
            "course" -> {
                if (id != null) {
                    navController.navigate(Routes.courseDetail(id))
                } else {
                    navController.navigate(Routes.COURSES)
                }
            }
            "profile" -> navController.navigate(Routes.PROFILE)
            "search"  -> navController.navigate(Routes.SEARCH)
            else      -> navController.navigate(Routes.HOME)
        }
    }

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
                onNavigateToHistory = { navController.navigate(Routes.LEARNING_HISTORY) },
                onNavigateToSupport = { navController.navigate(Routes.SUPPORT) },
                onNavigateToNotificationSetting = { navController.navigate(Routes.NOTIFICATION_SETTING) },
                onNavigateToPaymentSetting = { navController.navigate(Routes.PAYMENT_SETTING) },
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

        composable(Routes.LEARNING_HISTORY) {
            LearningHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Routes.courseDetail(courseId))
                },
                onNavigateToCertificates = { navController.navigate(Routes.MY_CERTIFICATES) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
            )
        }

        composable(Routes.SUPPORT) {
            SupportScreen(
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

        composable(Routes.NOTIFICATION_SETTING) {
            NotificationSettingScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

//        composable(Routes.PAYMENT_SETTING) {
//            PaymentSettingScreen(
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }

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
