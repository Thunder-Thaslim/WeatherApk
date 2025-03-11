package com.example.weather

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)


data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

data class Current(
    val temp_c: Float,
    val condition: Condition,
    val wind_kph: Double,  // ✅ Wind Speed (km/h)
    val humidity: Int,     // ✅ Humidity (%)
    val precip_mm: Double  // ✅ Precipitation (mm)
)


data class Forecast(
    val forecastday: List<ForecastDay>
)
data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,  // ✅ High temperature
    val mintemp_c: Double   // ✅ Low temperature
)

data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)

