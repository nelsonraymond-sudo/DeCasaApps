package com.example.decasaapps

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Setup Tombol Back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Menutup activity ini dan kembali ke Home
        }

        // 2. Menerima Data dari Halaman Home (Nanti)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Mille Housing"
        val location = intent.getStringExtra("EXTRA_LOCATION") ?: "Ago, Lagos"

        // 3. Pasang ke Teks
        findViewById<TextView>(R.id.tvDetailTitle).text = title
        findViewById<TextView>(R.id.tvDetailLocation).text = location

        // 4. Link ke Booking Page
        findViewById<android.widget.Button>(R.id.btnRent).setOnClickListener {
            val intent = android.content.Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        // 5. Link ke Rating Page
        findViewById<android.view.View>(R.id.layoutRating).setOnClickListener {
            val intent = android.content.Intent(this, RatingActivity::class.java)
            startActivity(intent)
        }
    }
}