package com.example.weather

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HourlyWeatherAdapter(private val hours: List<Hour>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourViewHolder>() {

    override fun getItemCount(): Int {
        Log.d("HourlyWeatherAdapter", "Total Hours: ${hours.size}") // Debugging
        return hours.size
    }

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.hourlyTime)
        val temp: TextView = view.findViewById(R.id.hourlyTemp)
        val icon: ImageView = view.findViewById(R.id.hourlyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather, parent, false)
        return HourViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hour = hours[position]

        holder.time.text = hour.time.substring(11)  // Extract HH:mm from time string
        holder.temp.text = "${hour.temp_c}°C"
        Glide.with(holder.itemView.context).load("https:${hour.condition.icon}").into(holder.icon)
        val iconUrl = "https:${hour.condition.icon}"
        Log.d("HourlyWeatherAdapter", "Icon URL: $iconUrl")



    }






}

