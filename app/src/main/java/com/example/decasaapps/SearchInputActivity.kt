package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.model.PropertyData

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

        // PERBAIKAN: Data dummy disesuaikan dengan Constructor PropertyData
        val dummyList = listOf(
            PropertyData("1", "Mille Housing", "Ago, Lagos", "400k", "Apartment", "https://dummyimage.com/600x400/000/fff", "4.5"),
            PropertyData("2", "Green Villa", "Ubud, Bali", "1.2M", "Villa", "https://dummyimage.com/600x400/000/fff", "4.8"),
            PropertyData("3", "Sunny House", "Jakarta", "850k", "House", "https://dummyimage.com/600x400/000/fff", "4.2")
        )

        // Pastikan Anda sudah punya 'TopSearchAdapter',
        // jika belum, ganti baris ini dengan PropertyAdapter yang sudah ada.
        // rvTopSearch.adapter = TopSearchAdapter(dummyList)

        // SEMENTARA: Gunakan SearchResultAdapter jika TopSearchAdapter belum diperbaiki
        rvTopSearch.adapter = SearchResultAdapter(dummyList)
    }
}