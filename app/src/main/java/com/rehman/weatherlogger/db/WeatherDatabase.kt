package com.rehman.weatherlogger.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//
//@Database(entities = [WeatherDataModel::class], version = 1)
//abstract class WeatherDatabase : RoomDatabase() {
//    abstract fun weatherDao(): WeatherDao
//
//    companion object {
//        private var INSTANCE: WeatherDatabase? = null
//        fun getDatabase(context: Context): WeatherDatabase {
//            if (INSTANCE == null) {
//                synchronized(this) {
//                    INSTANCE = Room.databaseBuilder(
//                        context, WeatherDatabase::class.java,
//                        "weather_database"
//                    ).createFromAsset("weather.db").build()
//                }
//            }
//            return INSTANCE!!
//        }
//    }
//}

@Database(entities = [WeatherDataModel::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


