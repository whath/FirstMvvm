package com.example.firstmvvm.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object WeatherNetWork {
    private val placeService = ServiceCreator.create(PlaceService::class.java)
    private val weatherServie = ServiceCreator.create(WeatherService::class.java)

    suspend fun searchPlace(query: String) = placeService.searchPlace(query).await()

    suspend fun getDailyWeather(lng:String,lat:String) = weatherServie.getDailyWeather(lng,lat).await()
    suspend fun getRealTimeWeather(lng:String,lat:String) = weatherServie.getRealtimeWeather(lng,lat).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("reponse body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(RuntimeException(t))
                }
            })
        }
    }
}