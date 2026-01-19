package com.example.decasaapps.model.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    @SerializedName("access_token")
    val token: String?,
    @SerializedName(value = "data", alternate = ["user"])
    val user: User?
)

data class RegisterRequest(
    @SerializedName("nm_user")
    val name: String,
    val email: String,
    val no_hp: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class RegisterResponse(
    val message: String?
)

data class User(
    @SerializedName(value = "id_user", alternate = ["id", "user_id", "id_customer", "customer_id"])
    val id: String?,
    @SerializedName(value = "nm_user", alternate = ["name", "nm_customer", "customer_name"])
    val name: String?,
    val email: String?,
    @SerializedName(value = "role", alternate = ["level"])
    val level: String?,
    val no_hp: String?
)
