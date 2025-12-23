package com.example.decasaapps.network

import com.example.decasaapps.PropertyData
import retrofit2.http.GET

interface ApiService {
    @GET("v1/4d4b757d-5a51-4994-b599-3d4a787e8bf6")
    suspend fun getAllProperties(): List<PropertyData>
}