package com.snoykuo.example.flightinfo.flight.datasource

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.snoykuo.example.flightinfo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val contentType = "application/json".toMediaType()

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("HttpLog", message)
    }.apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .callTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tdx.transportdata.tw/api/basic/v2/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val flightApi: FlightApiService = retrofit.create(FlightApiService::class.java)
    val airportApi: AirportApiService = retrofit.create(AirportApiService::class.java)
}