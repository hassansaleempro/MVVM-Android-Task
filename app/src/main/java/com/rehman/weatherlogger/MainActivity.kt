package com.rehman.weatherlogger

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rehman.weatherlogger.databinding.ActivityMainBinding
import com.rehman.weatherlogger.db.WeatherDataModel
import com.rehman.weatherlogger.utils.NetworkConnection

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private var binding: ActivityMainBinding? = null

    private var temperature: String = ""
    private var timeDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        prepareViewModel()
        observers()
        onClickListeners()


        if (hasLocationPermission()) {
            getUserLocation()
        } else {
            requestLocationPermission()
        }


    }

    private fun prepareViewModel() {
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {

        viewModel.temp.observe(this) { weatherTemp ->
            temperature = weatherTemp
            binding!!.weatherTemp.text = getString(R.string.temperature) + " $weatherTemp° C"
            Log.wtf("Api_Response", weatherTemp)
        }

        viewModel.date.observe(this) { weatherDate ->
            timeDate = weatherDate
            binding!!.fetchDate.text = getString(R.string.date) + " $weatherDate"
            Log.wtf("Api_Response", weatherDate)
        }
    }

    private fun onClickListeners() {
        binding!!.saveDate.setOnClickListener {
            val weatherData = WeatherDataModel(0, temperature, timeDate)
            viewModel.insertData(weatherData)
            Log.wtf("Api_Response", "Internet is connected latest data save in db $weatherData")

        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    makeRequest(latitude.toString(), longitude.toString())
                    Log.wtf(
                        "Current_Location",
                        "Current Location Lat:: $latitude , Long:: $longitude"
                    )
                }
            }
            .addOnFailureListener {
                Log.wtf("Current_Location", "Error ${it.localizedMessage}")
            }
    }

    private fun makeRequest(lat: String, long: String) {
        viewModel.requestWeatherData(lat, long)

    }


    private fun hasLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {

            showPermissionRationaleDialog()
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showPermissionRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission")
            .setMessage("This app requires location permission to function properly.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                getUserLocation()
            } else {
                showPermissionRationaleDialog()
            }
        }
    }

}