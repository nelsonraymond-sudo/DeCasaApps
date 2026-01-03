package com.example.decasaapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.model.Property

class HouseActivity : AppCompatActivity() {

    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private val resultList = ArrayList<Property>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // Reuse generic search layout

        // Setup Views
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(this)

        // Set Title specifically for House
        findViewById<android.widget.TextView>(R.id.tvPageTitle)?.text = "House"

        setupDummyData()

        adapter = SearchResultAdapter(resultList)
        rvSearchResults.adapter = adapter

        // Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
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
        // Only House items
        resultList.add(Property("Sunny House", "Jakarta, IND", "850k", R.drawable.apartement1, "House"))
        resultList.add(Property("Cozy Cottage", "Bandung, IND", "600k", R.drawable.apartement1, "House"))
        resultList.add(Property("Modern Housing", "New York, USA", "800k/year", R.drawable.apartement1, "House"))
        resultList.add(Property("Family Home", "Surabaya, IND", "450k", R.drawable.apartement1, "House"))
        resultList.add(Property("Grand Estate", "London, UK", "1.5M", R.drawable.apartement1, "House"))
    }
}
