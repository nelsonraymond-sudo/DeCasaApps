package com.example.decasaapps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }


        // Get Extras from DetailActivity
        val name = intent.getStringExtra("EXTRA_NAME")
        val location = intent.getStringExtra("EXTRA_LOCATION")
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")
        val price = intent.getStringExtra("EXTRA_PRICE")
        val id = intent.getStringExtra("EXTRA_ID")

        // Setup Booking Button
        findViewById<android.view.View>(R.id.btnBooking).setOnClickListener {
            val intent = android.content.Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("EXTRA_NAME", name)
            intent.putExtra("EXTRA_LOCATION", location)
            intent.putExtra("EXTRA_IMAGE", imageUrl)
            intent.putExtra("EXTRA_PRICE", price)
            intent.putExtra("EXTRA_ID", id)
            startActivity(intent)
        }

        // Setup Calendar
        setupCalendar()
    }

    private fun setupCalendar() {
        val rvCalendar = findViewById<RecyclerView>(R.id.rvCalendar)
        rvCalendar.layoutManager = GridLayoutManager(this, 7) // 7 days a week

        // Construct Dummy Data for July (matching screenshot)
        // Starts at 'R' (3rd column). So 2 empty slots.
        val days = ArrayList<String?>()
        days.add(null) // S
        days.add(null) // S
        
        for (i in 1..31) {
            days.add(i.toString())
        }

        rvCalendar.adapter = CalendarAdapter(days)
    }
}
