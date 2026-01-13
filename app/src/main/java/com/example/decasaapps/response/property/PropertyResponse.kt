package com.example.decasaapps.model.property // Sesuaikan dengan package kamu

import com.google.gson.annotations.SerializedName

// 1. Ini pembungkus utama sesuai response JSON
data class PropertyResponse(
    val success: Boolean,
    val message: String,
    val data: List<PropertyItem>
)

// 2. Ini detail setiap properti
data class PropertyItem(
    @SerializedName("id_properti")
    val idProperti: String,

    @SerializedName("nm_properti")
    val namaProperti: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("alamat")
    val alamat: String?,

    @SerializedName("harga")
    val harga: String, // Bisa String atau Double, aman String dulu

    @SerializedName("foto")
    val listFoto: List<FotoItem> = listOf(), // Default list kosong biar gak error kalau null

    @SerializedName("fasilitas")
    val listFasilitas: List<FasilitasItem> = listOf()
)

// 3. Ini model untuk Foto (Nested JSON)
data class FotoItem(
    @SerializedName("id_foto")
    val idFoto: Int,

    @SerializedName("url_foto")
    val urlFoto: String
)

// 4. Ini model untuk Fasilitas (Nested JSON)
data class FasilitasItem(
    @SerializedName("id_detail")
    val idDetail: Int,

    @SerializedName("id_fasilitas")
    val idFasilitas: String
)