package com.rehman.weatherlogger.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rehman.weatherlogger.modelClasses.WeatherModel

@Dao
interface WeatherDao {

    @Query("SELECT * from weather_data")
    fun getWeatherData(): WeatherDataModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataModel)
}