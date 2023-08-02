package com.rehman.weatherlogger.apiClasses

import com.rehman.weatherlogger.modelClasses.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("weather")
    suspend fun getWeatherData(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") appid: String? = "bd0ac96b33e6673fea9b151332702fda",
        @Query("units") units: String? = "metric"
    ): Response<WeatherModel>


}