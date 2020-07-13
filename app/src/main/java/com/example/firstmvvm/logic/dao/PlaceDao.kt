package com.example.firstmvvm.logic.dao

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.firstmvvm.MyApplication
import com.example.firstmvvm.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    fun savePlace(place: Place) {
        sharePreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavePlace(): Place {
        val string = sharePreferences().getString("place", "")
        return Gson().fromJson<Place>(string, Place::class.java)
    }

    fun isPlaceSaved() = sharePreferences().contains("place")

    private fun sharePreferences() =
        MyApplication.context.getSharedPreferences("my_weather", Context.MODE_PRIVATE)
}