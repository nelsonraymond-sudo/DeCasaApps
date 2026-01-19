package com.example.decasaapps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.adapter.PropertyAdapter
import com.example.decasaapps.client.Api
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.model.property.PropertyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val HOUSE_VARIANTS = listOf("House", "Rumah", "Building")

class HouseActivity : AppCompatActivity() {

    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: PropertyAdapter
    private var targetCategory: String = "House" // Default category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 0. Get Category from Intent
        targetCategory = intent.getStringExtra("CATEGORY") ?: "House"

        // 1. Inisialisasi RecyclerView
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(this)

        // 2. Setup Header
        findViewById<android.widget.EditText>(R.id.etSearchQuery).setText(targetCategory)
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // 3. Panggil Data
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        val apiService = RetrofitClient.instance.create(Api::class.java)

        apiService.getProperti().enqueue(object : Callback<PropertyResponse> {
            override fun onResponse(
                call: Call<PropertyResponse>,
                response: Response<PropertyResponse>
            ) {
                if (response.isSuccessful) {
                    val apiDataList = response.body()?.data

                    if (!apiDataList.isNullOrEmpty()) {

                        // Filter by Category
                        val filteredList = apiDataList.filter { item ->
                            val currentCat = item.kategori?.nmKategori?.trim()
                            if (targetCategory.equals("House", ignoreCase = true)) {
                                HOUSE_VARIANTS.any { it.equals(currentCat, ignoreCase = true) }
                            } else {
                                currentCat?.contains(targetCategory, ignoreCase = true) == true
                            }
                        }

                        if (filteredList.isNotEmpty()) {
                            val adapterList = filteredList.map { item ->
                                val urlFotoMentah = item.listFoto.firstOrNull()?.urlFoto
                                val fullUrl = if (urlFotoMentah != null) {
                                    "http://10.0.2.2:8000/storage/" + urlFotoMentah
                                } else {
                                    ""
                                }

                                PropertyData(
                                    serverId = item.idProperti,
                                    name = item.namaProperti,
                                    location = item.alamat ?: "Alamat tidak tersedia",
                                    price = item.harga ?: "0",
                                    imageUrl = fullUrl,
                                    rating = "4.5",
                                    description = item.deskripsi ?: "No Description",
                                    category = item.kategori?.nmKategori ?: "House",
                                    status = item.status ?: "Available",
                                    owner = item.namaPemilik ?: "DeCasa Admin",
                                    facilities = item.listFasilitas.mapNotNull { it.detail?.nmFasilitas }.joinToString(", ")
                                )
                            }

                            adapter = PropertyAdapter(adapterList) { _, _ -> }
                            rvSearchResults.adapter = adapter
                        } else {
                            // Show empty state if no items match category
                            Toast.makeText(this@HouseActivity, "No $targetCategory found", Toast.LENGTH_SHORT).show()
                            rvSearchResults.adapter = PropertyAdapter(emptyList()) { _, _ -> }
                        }

                    } else {
                        Toast.makeText(this@HouseActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HouseActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PropertyResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@HouseActivity, "Gagal koneksi: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}