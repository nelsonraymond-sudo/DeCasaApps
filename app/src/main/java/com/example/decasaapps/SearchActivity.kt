package com.example.decasaapps

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.model.PropertyData

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

        setupDummyData()

        adapter = SearchResultAdapter(resultList)
        rvSearchResults.adapter = adapter

        // Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Set Title based on intent
        val query = intent.getStringExtra("QUERY") ?: "Search"
        if (query.isNotEmpty()) {
            findViewById<TextView>(R.id.tvPageTitle)?.text = query
        }

        setupFilterButtons()
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

    private fun setupDummyData() {
        // PERBAIKAN: Constructor harus sesuai Model (id, nama, alamat, harga, deskripsi, fotoUrl, rating)
        // Foto URL harus String HTTP, jangan R.drawable (Int)
        val allProperties = listOf(
            PropertyData("1", "Mille Housing", "Ago, Lagos", "400k", "Apartment", "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", "4.5"),
            PropertyData("2", "Green Villa", "Ubud, Bali", "1.2M", "Villa", "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", "4.8"),
            PropertyData("3", "Sunny House", "Jakarta, IND", "850k", "House", "https://images.unsplash.com/photo-1580587771525-78b9dba3b91d", "4.3"),
            PropertyData("4", "Mille Housing II", "Ago, Lagos", "400k", "Apartment", "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", "4.4"),
            PropertyData("5", "Ocean View", "Kuta, Bali", "2.5M", "Villa", "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", "4.9"),
            PropertyData("6", "Cozy Cottage", "Bandung, IND", "600k", "House", "https://images.unsplash.com/photo-1580587771525-78b9dba3b91d", "4.2"),
            PropertyData("7", "Luxury House", "New York, USA", "800k", "House", "https://images.unsplash.com/photo-1564013799919-ab600027ffc6", "5.0")
        )

        val query = intent.getStringExtra("QUERY") ?: ""

        if (query.isNotEmpty() && query != "Search") {
            resultList.clear()
            // PERBAIKAN: Filter berdasarkan namaProperti atau Alamat (karena type/title tidak ada)
            resultList.addAll(allProperties.filter {
                it.namaProperti.contains(query, ignoreCase = true) ||
                        it.alamat.contains(query, ignoreCase = true) ||
                        (it.deskripsi?.contains(query, ignoreCase = true) == true)
            })
        } else {
            resultList.addAll(allProperties)
        }
    }
}