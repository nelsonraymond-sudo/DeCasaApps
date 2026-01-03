package com.example.decasaapps.network

import com.example.decasaapps.PropertyData
import com.example.decasaapps.model.auth.LoginRequest
import com.example.decasaapps.model.auth.LoginResponse
import com.example.decasaapps.model.auth.RegisterRequest
import com.example.decasaapps.model.auth.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("v1/4d4b757d-5a51-4994-b599-3d4a787e8bf6")
    suspend fun getAllProperties(): List<PropertyData>

    @POST("v3/6397330c-26f8-406a-a82f-8706d392345e") // Mocky endpoint for login
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("v3/42171500-1c9f-4428-97f2-149b5670868a") // Mocky endpoint for register
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
    
    @POST("v3/c70220c3-37e4-4dfb-930b-54070a248530") // Mocky endpoint for google login
    fun googleLogin(@Body token: String): Call<LoginResponse>
}