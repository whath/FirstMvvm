package com.example.firstmvvm.ui.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.firstmvvm.R
import com.example.firstmvvm.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }

    companion object {
        fun start(context: Context, lng: String, lat: String, place: String) {
            val intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra("location_lat", lat)
            intent.putExtra("location_lng", lng)
            intent.putExtra("place_name", place)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setContentView(R.layout.activity_weather)

        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerOpened(drawerView: View) {}
        })

        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat")
        }
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng")
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name")
        }
        viewModel.weatherLiveDate.observe(this, Observer {
            val weather = it.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法获得天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
            swipRefresh.isRefreshing = false
        })

        swipRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipRefresh.setOnRefreshListener {
            refreshWeather()
        }
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        val currentTempText = "${realtime.temperature.toInt()}°c"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        forecastLayout.removeAllViews()
        for (i in daily.skycon.indices) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            getSky(skycon.value).let {
                skyIcon.setImageResource(it.icon)
                skyInfo.text = it.info
            }
            temperatureInfo.text = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            forecastLayout.addView(view)
        }

        daily.lifeIndex.let {
            coldRiskText.text = it.coldRisk[0].desc
            dressingText.text = it.dressing[0].desc
            ultravioletText.text = it.ultraviolet[0].desc
            carWashingText.text = it.carWashing[0].desc
            weatherLayout.visibility = View.VISIBLE
        }
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipRefresh.isRefreshing = true
    }

}
