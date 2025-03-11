package com.example.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Call<WeatherResponse>

    @GET("search.json")
    fun searchLocation(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call<List<LocationResponse>>

    @GET("search.json")
    fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call<List<LocationItem>>

    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 1,  // Fetch 1-day forecast
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponse

}
