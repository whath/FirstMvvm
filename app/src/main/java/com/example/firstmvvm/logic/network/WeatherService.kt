package com.example.firstmvvm.logic.network

import com.example.firstmvvm.MyApplication
import com.example.firstmvvm.logic.model.DailyResponse
import com.example.firstmvvm.logic.model.RealTimeResponse
import com.example.firstmvvm.logic.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${MyApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealTimeResponse>

    @GET("v2.5/${MyApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}