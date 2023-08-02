package com.rehman.weatherlogger

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.rehman.weatherlogger.WeatherRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(application)
    val temp: LiveData<String>

    init {
        temp = repository.temp
    }


    fun requestWeatherData(lat: String, long: String) {
        repository.weatherRequest(lat, long)
        Log.wtf(
            "Current_Location",
            "View Model Lat:: $lat , Long:: $long"
        )
    }
}