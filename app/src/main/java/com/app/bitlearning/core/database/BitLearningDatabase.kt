/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.com.app.bitlearning.core.database // package com.app.bitlearning.core.database
//
// import androidx.room.Database
// import androidx.room.Room
// import androidx.room.RoomDatabase
// import android.content.Context
//
// /**
// * Room Database instance.
// * Currently defined with empty entities array as a placeholder.
// * Add entities here when local caching is needed (e.g., CourseEntity, LessonEntity).
// *
// * Example of how to add entities:
// *   @Database(entities = [CourseEntity::class, LessonEntity::class], version = 1)
// */
// @Database(entities = [], version = 1, exportSchema = false)
// abstract class BitLearningDatabase : RoomDatabase() {
//    // Define DAOs here when needed
//    // abstract fun courseDao(): CourseDao
//
//    companion object {
//        private const val DB_NAME = "bitlearning.db"
//
//        fun create(context: Context): BitLearningDatabase {
//            return Room.databaseBuilder(
//                context.applicationContext,
//                BitLearningDatabase::class.java,
//                DB_NAME
//            )
//                .fallbackToDestructiveMigration()
//                .build()
//        }
//    }
// }
