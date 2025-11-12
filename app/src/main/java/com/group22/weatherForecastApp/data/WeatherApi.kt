package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface WeatherApi {
    @GET("onecall")
    suspend fun getOneCallWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): OneCallResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/3.0/"

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}