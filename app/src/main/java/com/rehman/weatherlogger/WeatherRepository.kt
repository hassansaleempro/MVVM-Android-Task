package com.rehman.weatherlogger

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.rehman.weatherlogger.apiClasses.ApiService
import com.rehman.weatherlogger.db.WeatherDao
import com.rehman.weatherlogger.db.WeatherDataModel
import com.rehman.weatherlogger.db.WeatherDatabase
import com.rehman.weatherlogger.utils.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherRepository(private val application: Application, private val weatherDao: WeatherDao) {

    private val isInternetConnected = NetworkConnection(application)
    private var isRequesting: Boolean = false
    val temp = MutableLiveData<String>()
    val date = MutableLiveData<String>()


    fun weatherRequest(lat: String, long: String) {

        isInternetConnected.observeForever { isConnected ->
            if (isConnected) {
                Log.wtf("Api_Response", "Internet is connected")
                fetchDataFromApi(lat, long)
            } else {
                Log.wtf("Api_Response", "Internet is not connected")
                fetchDataFromDatabase()

            }
        }

    }

    private fun fetchDataFromApi(lat: String, long: String) {
        if (isInternetConnected.value == true) {
            CoroutineScope(Dispatchers.IO).launch {


                try {

                    Log.wtf(
                        "Current_Location",
                        "Try Block"
                    )

                    val response = ApiService
                        .getApiService().getWeatherData(lat, long)

                    withContext(Dispatchers.Main) {
                        isRequesting = false


                        when {
                            response.isSuccessful -> {
                                Log.wtf("Api_Response", "Success")

                                response.body()?.let { body ->
                                    Log.wtf("Api_Response", body.toString())

                                    temp.value = body.main!!.temp!!
                                    date.value = convertToFetchDate(body.dt, body.timezone)

//                                    saveDataToDatabase(temp.value, date.value)

                                    Log.wtf(
                                        "Api_Response", "Temp is ${temp.value} , dt is " +
                                                "${body.dt}, timezone is ${body.timezone}"
                                    )


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

    private fun fetchDataFromDatabase() {

        CoroutineScope(Dispatchers.IO).launch {


            val weatherData = getWeatherDataFromDatabase(application)
            if (weatherData != null) {


                CoroutineScope(Dispatchers.Main).launch {

                    Toast.makeText(application, "No Internet Connection", Toast.LENGTH_SHORT).show()


                    Log.wtf(
                        "Api_Response",
                        "Internet is not connected fetch data from db"
                    )

                    temp.value = weatherData.temp ?: ""
                    date.value = weatherData.date ?: ""

                    Toast.makeText(application, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun convertToFetchDate(dt: Long?, timezone: Long?): String {
        val timestampInMillis = dt!! * 1000 + timezone!! * 1000
        val date = Date(timestampInMillis)
        val sdf = SimpleDateFormat("dd MMM,yy - hh:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(date)
    }

    private fun isDatabaseFileExists(context: Context, dbName: String): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        return dbFile.exists()
    }

    private fun getWeatherDataFromDatabase(context: Context): WeatherDataModel? {
        val isDatabasePresent = isDatabaseFileExists(context, "weather_database")
        return if (isDatabasePresent) {
            val weatherDao = WeatherDatabase.getInstance(context).weatherDao()
            weatherDao.getWeatherData()
        } else {
            null
        }
    }

}