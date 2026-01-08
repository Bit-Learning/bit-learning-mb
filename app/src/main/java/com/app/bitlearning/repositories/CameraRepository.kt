/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.repositories

import com.app.bitlearning.models.Camera
import com.app.bitlearning.models.CameraType
import com.app.bitlearning.utils.SampleData
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CameraRepository {
    fun getAllCameras(): Flow<List<Camera>>
    fun getCameraById(id: Int): Flow<Camera?>
    fun getCamerasByType(type: CameraType): Flow<List<Camera>>
    fun getFeaturedCameras(): Flow<List<Camera>>
}

@Singleton
class CameraRepositoryImpl @Inject constructor() : CameraRepository {

    override fun getAllCameras(): Flow<List<Camera>> = flow {
        emit(SampleData.cameras)
    }

    override fun getCameraById(id: Int): Flow<Camera?> = flow {
        emit(SampleData.getCameraById(id))
    }

    override fun getCamerasByType(type: CameraType): Flow<List<Camera>> = flow {
        emit(SampleData.getCamerasByType(type))
    }

    override fun getFeaturedCameras(): Flow<List<Camera>> = flow {
        emit(SampleData.getFeaturedCameras())
    }
}
