package com.example.firstmvvm

import android.app.Application
import android.content.Context
import java.lang.invoke.ConstantCallSite

class MyApplication : Application() {

    companion object {
        lateinit var context: Context
        const val TOKEN = "my TOKEN"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}