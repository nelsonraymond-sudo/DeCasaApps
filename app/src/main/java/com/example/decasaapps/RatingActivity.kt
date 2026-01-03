package com.example.decasaapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RatingActivity : AppCompatActivity() {

    private lateinit var rvReviews: RecyclerView
    private lateinit var adapter: ReviewAdapter
    private val reviewList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvReviews = findViewById(R.id.rvReviews)
        rvReviews.layoutManager = LinearLayoutManager(this)

        // Setup Write Review Button
        findViewById<android.widget.Button>(R.id.btnWriteReview).setOnClickListener {
             val intent = android.content.Intent(this, AddReviewActivity::class.java)
             startActivity(intent)
        }

        setupDummyData()

        adapter = ReviewAdapter(reviewList)
        rvReviews.adapter = adapter
    }

    private fun setupDummyData() {
        reviewList.add(Review("Courtney Henry", "2 mins ago", 
            "UNI BAKWAN UNI BAKWAN UNI BAKWAN UNI BAKWAN", R.drawable.profile1))
        
        reviewList.add(Review("Cameron Williamson", "12 mins ago", 
            "Conseqat velit qui adipisicing sunt do rependerit ad laborum tempor ullamco.", R.drawable.profile1))
        
        reviewList.add(Review("Jane Cooper", "1 hour ago", 
            "Ullamco tempor adipisicing et voluptate duis sit esse aliqua esse ex.", R.drawable.profile1))
            
        reviewList.add(Review("Robert Fox", "1 day ago", 
            "Very good experience, highly recommended!", R.drawable.profile1))
    }
}
