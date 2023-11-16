package org.eu.noobshubham.todotroop.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    private fun getCurrentWeather(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val networkInterface: NetworkInterface = getCurrentWeather().create(NetworkInterface::class.java)
}