package com.example.decasaapps.network

import com.example.decasaapps.model.auth.LoginRequest
import com.example.decasaapps.model.auth.LoginResponse
import com.example.decasaapps.model.auth.RegisterRequest
import com.example.decasaapps.model.auth.RegisterResponse
import com.example.decasaapps.model.PropertyData
import com.example.decasaapps.model.property.PropertyResponse
import com.example.decasaapps.model.booking.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("properties")
    suspend fun getAllProperties(): List<PropertyData>

    @GET("properti")
    fun getProperti(): Call<PropertyResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/google")
    fun googleLogin(@Body token: String): Call<LoginResponse>

    @GET("properti")
    fun searchProperties(@Query("nama") query: String): Call<PropertyResponse>

    @GET("booked-dates/{id}")
    fun getBookedDates(@Path("id") propertyId: String): Call<BookedDatesResponse>

    @POST("transactions")
    fun storeTransaction(@Body request: TransactionRequest): Call<TransactionResponse>

    @GET("payment-methods")
    fun getPaymentMethods(): Call<PaymentMethodsResponse>

    @POST("transactions/preview")
    fun getBookingPreview(@Body request: PreviewRequest): Call<PreviewResponse>
}