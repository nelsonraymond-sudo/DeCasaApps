package com.example.decasaapps

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.adapter.PropertyAdapter
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.model.property.PropertyResponse
import com.example.decasaapps.network.ApiService
import com.example.decasaapps.PropertyData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private val resultList = ArrayList<PropertyData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Setup Views
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(this)

        adapter = SearchResultAdapter(resultList)
        rvSearchResults.adapter = adapter

        // Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Set Search Query
        val query = intent.getStringExtra("QUERY") ?: ""
        val etSearchQuery = findViewById<android.widget.EditText>(R.id.etSearchQuery)
        
        setupFilterButtons()

        if (query.isNotEmpty()) {
            etSearchQuery.setText(query)
            performSearch(query)
        } 
        
        // Handle New Search from inside Result Page
        etSearchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val newQuery = v.text.toString()
                if (newQuery.isNotEmpty()) {
                    performSearch(newQuery)
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setupFilterButtons() {
        val btnRentals = findViewById<TextView>(R.id.btnRentals)
        val btnBuy = findViewById<TextView>(R.id.btnBuy)
        val btnSell = findViewById<TextView>(R.id.btnSell)

        // Pastikan drawable 'button_selector' ada
        btnRentals.setBackgroundResource(R.drawable.button_selector)
        btnBuy.setBackgroundResource(R.drawable.button_selector)
        btnSell.setBackgroundResource(R.drawable.button_selector)

        val buttons = listOf(btnRentals, btnBuy, btnSell)

        fun selectButton(selected: TextView) {
            buttons.forEach { btn ->
                btn.isSelected = (btn == selected)
                btn.setTextColor(if (btn == selected) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            }
        }

        selectButton(btnRentals) // Default

        btnRentals.setOnClickListener { selectButton(btnRentals) }
        btnBuy.setOnClickListener { selectButton(btnBuy) }
        btnSell.setOnClickListener { selectButton(btnSell) }
    }

    private fun performSearch(query: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        
        // Use the searchProperties endpoint we added
        apiService.searchProperties(query).enqueue(object : Callback<PropertyResponse> {
            override fun onResponse(call: Call<PropertyResponse>, response: Response<PropertyResponse>) {
                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    val dataServer = propertyResponse?.data

                    resultList.clear()
                    if (!dataServer.isNullOrEmpty()) {
                        val mappedData = dataServer.map { item ->
                            val fotoUrlPath = item.listFoto.firstOrNull()?.urlFoto
                            val fullFotoUrl = if (fotoUrlPath != null) {
                                "http://10.0.2.2:8000/storage/" + fotoUrlPath
                            } else {
                                "" 
                            }

                            PropertyData(
                                serverId = item.idProperti,
                                name = item.namaProperti,
                                location = item.alamat ?: "Alamat tidak tersedia",
                                price = item.harga ?: "0", 
                                rating = "4.5",
                                imageUrl = fullFotoUrl,
                                description = item.deskripsi ?: "No Description",
                                category = item.kategori?.nmKategori ?: "House",
                                status = item.status ?: "Available",
                                owner = item.namaPemilik ?: "DeCasa Admin",
                                facilities = item.listFasilitas.mapNotNull { it.detail?.nmFasilitas }.joinToString(", ")
                            )
                        }
                        resultList.addAll(mappedData)
                        adapter.notifyDataSetChanged()
                    } else {
                         Toast.makeText(this@SearchActivity, "No results found", Toast.LENGTH_SHORT).show()
                         adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("SearchActivity", "Error: ${response.message()}")
                    Toast.makeText(this@SearchActivity, "Search failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PropertyResponse>, t: Throwable) {
                Log.e("SearchActivity", "Failure: ${t.message}")
                Toast.makeText(this@SearchActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}