package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    val api: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Add OkHttpClient with timeouts and error handling
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }
}

