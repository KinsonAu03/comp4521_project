package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
    
    // Configure OkHttpClient with proper timeouts and error handling
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Only log in debug builds to avoid performance issues in production
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)        // Retry on connection failure
            .build()
    }

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Add OkHttpClient with timeouts
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}