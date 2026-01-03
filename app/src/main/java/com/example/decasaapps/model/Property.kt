package com.example.decasaapps.model

data class Property(
    val title: String,
    val location: String,
    val price: String,
    val imageResId: Int,
    val type: String = "Apartment" // Default value
)
