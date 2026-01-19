package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.network.ApiService
import com.example.decasaapps.model.property.PropertyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchInputActivity : AppCompatActivity() {

    private lateinit var rvTopSearch: RecyclerView
    private lateinit var tvFoundCount: TextView
    private val topSearchList = ArrayList<PropertyData>()
    private lateinit var adapter: TopSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_input)

        val btnBack = findViewById<android.view.View>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val etSearch = findViewById<EditText>(R.id.etSearchQuery)
        tvFoundCount = findViewById(R.id.tvFoundCount)

        // Handle Action Search (Enter key on keyboard)
        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)
                return@setOnEditorActionListener true
            }
            false
        }

        // Setup Top Search List
        rvTopSearch = findViewById(R.id.rvTopSearch)
        rvTopSearch.layoutManager = LinearLayoutManager(this)
        
        adapter = TopSearchAdapter(topSearchList)
        rvTopSearch.adapter = adapter

        fetchTopSearchData()
    }

    private fun fetchTopSearchData() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        
        apiService.getProperti().enqueue(object : Callback<PropertyResponse> {
            override fun onResponse(call: Call<PropertyResponse>, response: Response<PropertyResponse>) {
                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    val dataDariServer = propertyResponse?.data

                    if (!dataDariServer.isNullOrEmpty()) {
                        val mappedData = dataDariServer.map { itemServer ->
                            val fotoUrlPath = itemServer.listFoto.firstOrNull()?.urlFoto
                            val fullFotoUrl = if (fotoUrlPath != null) {
                                "http://10.0.2.2:8000/storage/" + fotoUrlPath
                            } else {
                                "" 
                            }

                            PropertyData(
                                serverId = itemServer.idProperti,
                                name = itemServer.namaProperti,
                                location = itemServer.alamat ?: "Alamat tidak tersedia",
                                price = itemServer.harga ?: "0",
                                rating = "4.5",
                                imageUrl = fullFotoUrl,
                                description = itemServer.deskripsi ?: "No Description",
                                category = itemServer.kategori?.nmKategori ?: "No Category",
                                status = itemServer.status ?: "Available",
                                owner = itemServer.namaPemilik ?: "DeCasa Admin",
                                facilities = itemServer.listFasilitas.mapNotNull { it.detail?.nmFasilitas }.joinToString(", ")
                            )
                        }

                        topSearchList.clear()
                        topSearchList.addAll(mappedData)
                        adapter.notifyDataSetChanged()
                        
                        tvFoundCount.text = "${mappedData.size} found"
                    } else {
                        tvFoundCount.text = "0 found"
                    }
                } else {
                    Log.e("SearchInputActivity", "API Error: ${response.message()}")
                    Toast.makeText(this@SearchInputActivity, "Failed to load top search", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PropertyResponse>, t: Throwable) {
                Log.e("SearchInputActivity", "Failure: ${t.message}")
                Toast.makeText(this@SearchInputActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
