package com.example.decasaapps.network


import com.example.decasaapps.model.auth.LoginRequest
import com.example.decasaapps.model.auth.LoginResponse
import com.example.decasaapps.model.auth.RegisterRequest
import com.example.decasaapps.model.auth.RegisterResponse
import com.example.decasaapps.model.PropertyData
import com.example.decasaapps.model.property.PropertyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/properties")
    suspend fun getAllProperties(): List<PropertyData>

    @GET("properti")
    fun getProperti(): Call<PropertyResponse>

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/google")
    fun googleLogin(@Body token: String): Call<LoginResponse>
}