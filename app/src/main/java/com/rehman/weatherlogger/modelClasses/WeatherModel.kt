package com.rehman.weatherlogger.modelClasses

data class WeatherModel(
    val main: Main? = null,
    val dt: Long? = null,
    val timezone: Long? = null,
)

data class Main(
    val temp: String? = null
)
