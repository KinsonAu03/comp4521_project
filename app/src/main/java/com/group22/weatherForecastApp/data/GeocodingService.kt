package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Service for geocoding operations using OpenWeatherMap Geocoding API
 * Used to search for locations by name
 */
interface GeocodingApi {
    @GET("direct")
    suspend fun searchLocations(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): List<GeocodingResponse>
}

data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String? = null,
    val state: String? = null,
    val local_names: Map<String, String>? = null
)

object GeocodingClient {
    private const val BASE_URL = "https://api.openweathermap.org/geo/1.0/"

    val api: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }
}

