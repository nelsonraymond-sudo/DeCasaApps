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
    val harga: String?, // Nullable String to be safe

    @SerializedName("status")
    val status: String?, 

    @SerializedName("nama_pemilik")
    val namaPemilik: String?,

    @SerializedName("kategori")
    val kategori: KategoriItem?,

    @SerializedName("foto")
    val listFoto: List<FotoItem> = listOf(),

    @SerializedName("fasilitas")
    val listFasilitas: List<FasilitasWrapper> = listOf() 
)

data class FotoItem(
    @SerializedName("id_foto")
    val idFoto: String?, // Changed to String

    @SerializedName("url_foto")
    val urlFoto: String?
)

data class FasilitasWrapper(
    @SerializedName("id_detail")
    val idDetail: String?, // Changed to String

    @SerializedName("fasilitas")
    val detail: FasilitasDetail?
)

data class FasilitasDetail(
    @SerializedName("id_fasilitas")
    val idFasilitas: String?,

    @SerializedName("nm_fasilitas")
    val nmFasilitas: String?
)

data class KategoriItem(
    @SerializedName("id_kategori")
    val idKategori: String?, // Changed to String

    @SerializedName("nm_kategori")
    val nmKategori: String?
)