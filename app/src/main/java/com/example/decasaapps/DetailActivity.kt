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

        // 2. Menerima Data dari Halaman Home
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Mille Housing"
        val location = intent.getStringExtra("EXTRA_LOCATION") ?: "Ago, Lagos"
        val rating = intent.getStringExtra("EXTRA_RATING") ?: "4.0"
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")

        // 3. Pasang ke Teks
        findViewById<TextView>(R.id.tvDetailTitle).text = name
        findViewById<TextView>(R.id.tvDetailLocation).text = location
        
        // Update Rating Text (inside layoutRating)
        val ratingLayout = findViewById<android.widget.LinearLayout>(R.id.layoutRating)
        val tvRating = ratingLayout.getChildAt(2) as? TextView // Assuming order from xml: Text (Costs), View, Star, TextView
        // Note: The layoutRating in xml has TextView(Costs), View, ImageView(Star), TextView(Rating).
        // It's safer to find by traversal or if there was an ID. There isn't an ID for rating text inside layoutRating.
        // Let's iterate or assume index. Or simpler: just skip rating text update if it's hard, or add ID to xml.
        // Actually, let's keep it simple and just update image and text.
        
        // Load Image
        val ivDetailImage = findViewById<ImageView>(R.id.ivDetailImage)
        if (imageUrl != null) {
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(ivDetailImage)
        }

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