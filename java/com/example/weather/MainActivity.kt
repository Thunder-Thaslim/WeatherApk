package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import com.squareup.picasso.Picasso
import android.widget.ImageView
//import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import android.widget.AutoCompleteTextView
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private val API_KEY = "2767506e97c34e31b5694653250103"
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hourlyAdapter: HourlyWeatherAdapter

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ConstaintLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val locationInput = findViewById<AutoCompleteTextView>(R.id.editLocation)
        val button = findViewById<TextView>(R.id.button2)
        val weatherText = findViewById<TextView>(R.id.textView)
        val weatherText1 = findViewById<TextView>(R.id.textView1)
        val weatherText2 = findViewById<TextView>(R.id.textView2)
        val weatherText3 = findViewById<TextView>(R.id.textView3)
        val weatherIcon = findViewById<ImageView>(R.id.imageView2)



        val apiKey = "2767506e97c34e31b5694653250103"

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        locationInput.setAdapter(adapter)


        locationInput.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = adapter.getItem(position)
            locationInput.setText(selectedCity, false)
        }

        locationInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) >= 2) {
                    fetchLocationSuggestions(apiKey, s.toString(), adapter)
                }
            }
        })


        button.setOnClickListener {
            val location = locationInput.text.toString()
            if (location.isNotEmpty()) {
                fetchWeatherData(apiKey, location, weatherText2)
            }
            viewModel.fetchWeather(API_KEY, location)
        }

        var conditionText: String? = null

        viewModel.weatherData.observe(this) { weather ->
            weatherText.text = weather.location.name
            weatherText3.text = "${weather.current.temp_c}°C"
            weatherText1.text = weather.current.condition.text
            weather.current.condition.text.also { conditionText = it }
            val weatherDrawable = getWeatherIconResource(conditionText.toString())
            weatherIcon.setImageResource(weatherDrawable)
            hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView)
            hourlyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            hourlyRecyclerView.addItemDecoration(RecyclerViewItemDecoration(5))
            fetchWeatherData(weather.location.name)
            //Picasso.get().load("https:${weather.current.condition.icon}").into(weatherIcon)

        }



        Log.d("WeatherApp", "Condition text: $conditionText")





        locationInput.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    val query = locationInput.text.toString().trim()
                    if (query.isNotEmpty()) {
                        searchWeather(query)  // Call your search function
                    } else {
                        Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    false
                }
            }
        }


    private fun fetchWeatherData(apiKey: String, location: String, textView: TextView) {
        RetrofitClient.instance.getCurrentWeather(apiKey, location).enqueue(object :
            Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val dateTime = response.body()?.location?.localtime
                    textView.text = "$dateTime"
                } else {
                    textView.text = "City not found"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                textView.text = "Error fetching data"
            }
        })

        RetrofitClient.instance.searchLocations(apiKey, location).enqueue(object :
            Callback<List<LocationItem>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<List<LocationItem>>,
                response: Response<List<LocationItem>>
            ) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val selectedLocation = response.body()?.first()
                    textView.text =
                        "Selected: ${selectedLocation?.name}, ${selectedLocation?.country}"
                } else {
                    textView.text = "City not found"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<List<LocationItem>>, t: Throwable) {
                textView.text = "Error fetching data"
            }
        })
    }

        private fun searchWeather(city: String) {
            Toast.makeText(this, "Searching weather for: $city", Toast.LENGTH_SHORT).show()
            // Add your API call here
        }
    private fun fetchLocationSuggestions(apiKey: String, query: String, adapter: ArrayAdapter<String>) {
        RetrofitClient.instance.searchLocations(apiKey, query).enqueue(object :
            Callback<List<LocationItem>> {
            override fun onResponse(call: Call<List<LocationItem>>, response: Response<List<LocationItem>>) {
                if (response.isSuccessful) {
                    val locationNames = response.body()?.map { "${it.name}, ${it.country}" } ?: emptyList()
                    adapter.clear()
                    adapter.addAll(locationNames)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<LocationItem>>, t: Throwable) {
                // Handle failure
            }
        })
    }


    private fun getWeatherIconResource(conditionText: String): Int {
        return when (conditionText.lowercase()) { // Convert to lowercase for matching
            "sunny" -> R.drawable.sunny
            "clear" -> R.drawable.clear // Nighttime clear sky can also be "Clear"
            "partly cloudy" -> R.drawable.cloudy_sunny
            "cloudy", "patchy rain nearby" -> R.drawable.cloudy
            "overcast" -> R.drawable.overcast
            "mist", "fog", "freezing fog" -> R.drawable.mist
            "patchy rain possible", "light rain", "moderate rain", "patchy light drizzle" -> R.drawable.light_rain
            "heavy rain", "torrential rain shower" -> R.drawable.rainy
            "patchy snow possible", "light snow", "moderate snow" -> R.drawable.snowy
            "thunderstorm", "patchy light rain with thunder", "moderate or heavy rain with thunder" -> R.drawable.thunderstorm
            else -> R.drawable.sunny // Default icon for unknown conditions
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun fetchWeatherData(location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getWeather(API_KEY, location)
                val hourlyData = response.forecast.forecastday[0].hour

                if (response.forecast.forecastday.isNotEmpty()) {
                    val todayForecast = response.forecast.forecastday[0]

                    val highTemp = todayForecast.day.maxtemp_c
                    val lowTemp = todayForecast.day.mintemp_c

                    Log.d("WeatherApp", "High Temp: $highTemp°C, Low Temp: $lowTemp°C")

                    withContext(Dispatchers.Main) {
                        findViewById<TextView>(R.id.textView4_1).text = "H: ${highTemp}°C"
                        findViewById<TextView>(R.id.textView4_2).text = "L: ${lowTemp}°C"
                    }
                }
                val currentWeather = response.current

                val windSpeed = currentWeather.wind_kph
                val humidity = currentWeather.humidity
                val precipitation = currentWeather.precip_mm

                Log.d("WeatherApp", "Wind: $windSpeed km/h, Humidity: $humidity%, Precipitation: $precipitation mm")

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textView7).text = "$windSpeed km/h"
                    findViewById<TextView>(R.id.textView9).text = "${humidity}%"
                    findViewById<TextView>(R.id.textView5).text = "$precipitation mm"
                }

                Log.d("WeatherFragment", "Hourly Data Count: ${hourlyData.size}")
                for (hour in hourlyData) {
                    Log.d("WeatherFragment", "Hour: ${hour.time} Temp: ${hour.temp_c}°C")
                }

                withContext(Dispatchers.Main) {
                    hourlyAdapter = HourlyWeatherAdapter(hourlyData)
                    hourlyRecyclerView.adapter = hourlyAdapter
                    hourlyAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error fetching data", e)
            }
        }
    }

}








