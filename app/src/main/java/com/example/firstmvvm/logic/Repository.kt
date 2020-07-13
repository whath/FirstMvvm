package com.example.firstmvvm.logic


import androidx.lifecycle.liveData
import com.example.firstmvvm.logic.dao.PlaceDao
import com.example.firstmvvm.logic.model.Place
import com.example.firstmvvm.logic.model.Weather
import com.example.firstmvvm.logic.network.WeatherNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object Repository {
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {

        val placeResponse = WeatherNetWork.searchPlace(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealTime = async {
                WeatherNetWork.getRealTimeWeather(lng, lat)
            }
            val deferredDaily = async {
                WeatherNetWork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealTime.await()
            val dailyResponse = deferredDaily.await()

            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" + "dailytime response status is ${dailyResponse.status}"))
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getSavePlace()

    fun isPlaceSaved()=PlaceDao.isPlaceSaved()

    private fun <T> fire(context1: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context1) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}