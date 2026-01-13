package com.example.decasaapps.model

data class PropertyData(
    val id: String,
    val namaProperti: String,
    val alamat: String,
    val harga: String,
    val deskripsi: String?,
    val fotoUrl: String,
    val rating: String? = "0.0"
)