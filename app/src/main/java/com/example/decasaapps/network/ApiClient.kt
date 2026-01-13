package com.example.decasaapps.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Base URL Mocky (Pasti sama untuk semua orang)
    // Base URL Laravel (10.0.2.2 is localhost for Emulator)
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val instance: ApiService by lazy {

        // Logger supaya kita bisa lihat request/response di Logcat (Penting buat debugging)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}