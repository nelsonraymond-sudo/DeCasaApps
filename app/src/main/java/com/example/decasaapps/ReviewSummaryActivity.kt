package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReviewSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_summary)

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Setup Change Payment Button
        findViewById<android.view.View>(R.id.btnChangePayment).setOnClickListener {
            finish() // Simply go back to Payment Activity
        }

        // Setup Continue Button
        findViewById<android.view.View>(R.id.btnContinue).setOnClickListener {
            // Navigate to E-Receipt
            val intent = Intent(this, EReceiptActivity::class.java)
            startActivity(intent)
        }
    }
}
