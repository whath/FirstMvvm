package com.example.firstmvvm.logic.network

import com.example.firstmvvm.MyApplication
import com.example.firstmvvm.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${MyApplication.TOKEN}&lang=zh_CN")
    fun searchPlace(@Query("query")query:String): Call<PlaceResponse>
}