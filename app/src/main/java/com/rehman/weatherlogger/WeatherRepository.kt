package com.rehman.weatherlogger

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rehman.weatherlogger.apiClasses.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherRepository(application: Application) {

    private val url = "https://api.openweathermap.org/data/2.5/"

    private var isRequesting: Boolean = false
    val temp = MutableLiveData<String>()

    fun weatherRequest(lat: String, long: String) {
        Log.wtf(
            "Current_Location",
            "Repo Lat:: $lat , Long:: $long"
        )
        if (!isRequesting) {
            isRequesting = true


            CoroutineScope(Dispatchers.IO).launch {


                try {

                    Log.wtf(
                        "Current_Location",
                        "Try Block"
                    )

                    val response = ApiService
                        .getApiService(url).getWeatherData(lat, long)

                    withContext(Dispatchers.Main) {
                        isRequesting = false


                        when {
                            response.isSuccessful -> {
                                Log.wtf("Api_Response", "Success")

                                response.body()?.let { body ->
                                    Log.wtf("Api_Response", body.toString())

                                    temp.value = body.main!!.temp

                                    Log.wtf("Api_Response", "Temp is ${temp.value}")

                                }
                            }

                            response.code() == 400 -> {
                                Log.wtf("Api_Response", "400 (Not Found)")


                            }

                            response.code() in 500..599 -> {
                                Log.wtf("Api_Response", "500 - 599 (Server Error)")


                            }

                            else -> {
                                Log.wtf("Api_Response", "Unexpected Error")
                            }
                        }


                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isRequesting = false
                        Log.wtf(
                            "Current_Location",
                            "Cacth Block ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

}