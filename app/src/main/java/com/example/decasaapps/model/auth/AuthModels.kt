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
    @SerializedName("data")
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
    @SerializedName("id_user")
    val id: String,
    @SerializedName("nm_user")
    val name: String,
    val email: String,
    @SerializedName("role")
    val level: String,
    val no_hp: String?
)
