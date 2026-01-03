package com.example.decasaapps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etReview = findViewById<EditText>(R.id.etReview)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnAddPhoto = findViewById<android.view.View>(R.id.btnAddPhoto)

        btnAddPhoto.setOnClickListener {
            Toast.makeText(this, "Select Photo clicked", Toast.LENGTH_SHORT).show()
        }

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val review = etReview.text.toString()

            if (rating == 0f) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (review.isEmpty()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mock Data Submission
            Toast.makeText(this, "Review Submitted!", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
