package com.example.decasaapps

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.model.Property

class SearchActivity : AppCompatActivity() {

    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private val resultList = ArrayList<Property>()

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
            findViewById<android.widget.TextView>(R.id.tvPageTitle)?.text = query
        }

        setupFilterButtons()
    }

    private fun setupFilterButtons() {
        val btnRentals = findViewById<android.widget.TextView>(R.id.btnRentals)
        val btnBuy = findViewById<android.widget.TextView>(R.id.btnBuy)
        val btnSell = findViewById<android.widget.TextView>(R.id.btnSell)

        // Set Selector Drawable
        btnRentals.setBackgroundResource(R.drawable.button_selector)
        btnBuy.setBackgroundResource(R.drawable.button_selector)
        btnSell.setBackgroundResource(R.drawable.button_selector)

        // Add Click Listeners
        val buttons = listOf(btnRentals, btnBuy, btnSell)
        
        fun selectButton(selected: android.widget.TextView) {
            buttons.forEach { btn ->
                btn.isSelected = (btn == selected)
                btn.setTextColor(if (btn == selected) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            }
        }

        // Default selection
        selectButton(btnRentals)

        btnRentals.setOnClickListener { selectButton(btnRentals) }
        btnBuy.setOnClickListener { selectButton(btnBuy) }
        btnSell.setOnClickListener { selectButton(btnSell) }
    }

    private fun setupDummyData() {
        val allProperties = listOf(
            Property("Mille Housing", "Ago, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Green Villa", "Ubud, Bali", "1.2M", R.drawable.apartement1, "Villa"),
            Property("Sunny House", "Jakarta, IND", "850k", R.drawable.apartement1, "House"),
            Property("Mille Housing", "Ago, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Ocean View", "Kuta, Bali", "2.5M", R.drawable.apartement1, "Villa"),
            Property("Cozy Cottage", "Bandung, IND", "600k", R.drawable.apartement1, "House"),
            // Extra House items to match screenshot volume
            Property("Mille Housing", "Ago, Lagos", "400k/year", R.drawable.apartement1, "House"),
            Property("Modern Housing", "New York, USA", "800k/year", R.drawable.apartement1, "House")
        )

        val query = intent.getStringExtra("QUERY") ?: ""
        
        if (query.isNotEmpty() && query != "Search") {
            // Filter list based on query (property type)
            // Case insensitive comparison
            resultList.clear()
            resultList.addAll(allProperties.filter { 
                it.type.equals(query, ignoreCase = true) || it.title.contains(query, ignoreCase = true)
            })
        } else {
            resultList.addAll(allProperties)
        }
    }
}