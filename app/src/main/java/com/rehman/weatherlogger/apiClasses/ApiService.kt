package com.rehman.weatherlogger.apiClasses

object ApiService {
    private const val url = "https://api.openweathermap.org/data/2.5/"

    fun getApiService(): RetrofitService = RetrofitClient()
        .retrofitClient(url)
        .create(RetrofitService::class.java)



}