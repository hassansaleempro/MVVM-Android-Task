package com.rehman.weatherlogger

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rehman.weatherlogger.db.WeatherDao
import com.rehman.weatherlogger.db.WeatherDataModel
import com.rehman.weatherlogger.db.WeatherDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository
    private val weatherDao: WeatherDao
    val temp: LiveData<String>
    val date: LiveData<String>

    init {
        weatherDao = WeatherDatabase.getInstance(application).weatherDao()
        repository = WeatherRepository(application, weatherDao)
        temp = repository.temp
        date = repository.date
    }


    fun requestWeatherData(lat: String, long: String) {
        repository.weatherRequest(lat, long)
        Log.wtf(
            "Current_Location",
            "View Model Lat:: $lat , Long:: $long"
        )
    }

    fun insertData(entity: WeatherDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.insertWeatherData(entity)
        }
    }

}