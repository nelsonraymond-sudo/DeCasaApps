package com.example.decasaapps.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // URL 1: For 'php artisan serve' (Port 8000) - RECOMMENDED
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    // URL 2: For XAMPP (Only if you are sure about the folder path)
    // private const val BASE_URL = "http://10.0.2.2/DeCasa-WebProperty/public/"

    // Static token to be set after login
    var token: String? = null

    val instance: ApiService by lazy {

        // Logger supaya kita bisa lihat request/response di Logcat (Penting buat debugging)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                
                // Add Authorization header if token is available
                token?.let {
                    android.util.Log.d("NETWORK_LOG", "Adding Token: Bearer $it")
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                } ?: run {
                    android.util.Log.w("NETWORK_LOG", "Token is NULL - Request to ${chain.request().url}")
                }
                
                chain.proceed(requestBuilder.build())
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}