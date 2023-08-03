package com.rehman.weatherlogger.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("weather_data")
data class WeatherDataModel(

    @PrimaryKey
    val weatherID: Int = 0,
    val temp: String?,
    val date: String?


)
