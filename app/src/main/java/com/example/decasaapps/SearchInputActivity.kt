package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.model.Property

class SearchInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_input)

        val btnBack = findViewById<android.view.View>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val etSearch = findViewById<EditText>(R.id.etSearchQuery)
        
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
        val rvTopSearch = findViewById<RecyclerView>(R.id.rvTopSearch)
        rvTopSearch.layoutManager = LinearLayoutManager(this)

        val dummyList = listOf(
            Property("Mille Housing", "No 20 Okiki Street, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Mille Housing", "No 20 Okiki Street, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Mille Housing", "No 20 Okiki Street, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Mille Housing", "No 20 Okiki Street, Lagos", "400k", R.drawable.apartement1, "Apartment"),
            Property("Mille Housing", "No 20 Okiki Street, Lagos", "400k", R.drawable.apartement1, "Apartment")
        )
        
        rvTopSearch.adapter = TopSearchAdapter(dummyList)
    }
}
