package com.rehman.weatherlogger.apiClasses

object ApiService {

    fun getApiService(url: String): RetrofitService = RetrofitClient()
        .retrofitClient(url)
        .create(RetrofitService::class.java)



}