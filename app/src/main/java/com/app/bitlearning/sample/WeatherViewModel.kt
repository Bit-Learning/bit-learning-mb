/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.sample

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _temperature = mutableStateOf<Double?>(null)
    val temperature: State<Double?> = _temperature

    private val _weather = mutableStateOf<WeatherResult?>(null)
    val weather: State<WeatherResult?> = _weather

    init {
        fetchWeather()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTemperature() {
        viewModelScope.launch {
            try {
                _temperature.value = repository.getTemperature(10.823, 106.6296)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error: ${e.message}")
            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                _weather.value = repository.getCurrentWeather(10.823, 106.6296)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error: ${e.message}")
            }
        }
    }
}
