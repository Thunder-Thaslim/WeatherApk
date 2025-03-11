package com.example.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _locationResults = MutableLiveData<List<LocationResponse>>()
    val locationResults: LiveData<List<LocationResponse>> get() = _locationResults

    fun fetchWeather(apiKey: String, location: String) {
        RetrofitClient.instance.getCurrentWeather(apiKey, location).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    fun searchLocation(apiKey: String, query: String) {
        RetrofitClient.instance.searchLocation(apiKey, query).enqueue(object : Callback<List<LocationResponse>> {
            override fun onResponse(call: Call<List<LocationResponse>>, response: Response<List<LocationResponse>>) {
                if (response.isSuccessful) {
                    _locationResults.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<LocationResponse>>, t: Throwable) {
                // Handle error
            }
        })
    }
}
