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
import com.example.decasaapps.client.Api // Pastikan ini sesuai nama Interface API kamu (misal ApiService)
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.model.PropertyData
import com.example.decasaapps.model.property.PropertyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HouseActivity : AppCompatActivity() {

    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // Pastikan ID RecyclerView ada di file layout ini

        // 1. Inisialisasi RecyclerView
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(this)

        // 2. Setup Header
        findViewById<TextView>(R.id.tvPageTitle)?.text = "House Category"
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // 3. Panggil Data
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        // Ganti 'Api::class.java' dengan 'ApiService::class.java' jika itu nama file kamu
        val apiService = RetrofitClient.instance.create(Api::class.java)

        apiService.getProperti().enqueue(object : Callback<PropertyResponse> {
            override fun onResponse(
                call: Call<PropertyResponse>,
                response: Response<PropertyResponse>
            ) {
                if (response.isSuccessful) {
                    val apiDataList = response.body()?.data

                    if (!apiDataList.isNullOrEmpty()) {

                        // === PERBAIKAN LOGIC MAPPING DI SINI ===
                        val adapterList = apiDataList.map { item ->

                            // 1. Ambil foto pertama dari list, lalu gabungkan dengan Base URL
                            val urlFotoMentah = item.listFoto.firstOrNull()?.urlFoto
                            val fullUrl = if (urlFotoMentah != null) {
                                "http://10.0.2.2:8000/storage/" + urlFotoMentah
                            } else {
                                ""
                            }

                            // 2. Masukkan ke PropertyData (Model UI)
                            PropertyData(
                                id = item.idProperti, // Pastikan tipe datanya sama (String/Int)
                                namaProperti = item.namaProperti,
                                alamat = item.alamat ?: "Alamat tidak tersedia",
                                harga = "Rp ${item.harga}", // Tambah format Rp biar cantik
                                deskripsi = item.deskripsi,
                                fotoUrl = fullUrl, // Gunakan URL yang sudah dirakit di atas
                                rating = "4.5"
                            )
                        }

                        // Set Adapter
                        adapter = PropertyAdapter(adapterList)
                        rvSearchResults.adapter = adapter

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