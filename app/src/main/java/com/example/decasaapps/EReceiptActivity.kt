package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ereceipt)

        // Retrieve Data
        val propertyName = intent.getStringExtra("EXTRA_PROPERTY_NAME") ?: "Mille Housing"
        val price = intent.getStringExtra("EXTRA_PRICE") ?: "$0"
        val dateMillis = intent.getLongExtra("EXTRA_DATE", System.currentTimeMillis())
        val txId = intent.getStringExtra("EXTRA_TRANSACTION_ID") ?: "000000000000"

        // Setup Views
        val tvDate = findViewById<android.widget.TextView>(R.id.tvReceiptDate)
        val tvAmount = findViewById<android.widget.TextView>(R.id.tvReceiptAmount)
        val tvTotal = findViewById<android.widget.TextView>(R.id.tvReceiptTotal)
        val tvTxId = findViewById<android.widget.TextView>(R.id.tvReceiptTransactionId)
        
        // Format Date
        val sdf = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
        val dateString = sdf.format(java.util.Date(dateMillis))

        // Set Data
        tvDate.text = dateString
        tvAmount.text = price
        tvTotal.text = price // Assuming total is same as price for now, or calculate tax if needed
        tvTxId.text = txId

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            navigateHome()
        }

        // Setup Download Button
        findViewById<android.view.View>(R.id.btnDownload).setOnClickListener {
            Toast.makeText(this, "Downloading Receipt...", Toast.LENGTH_SHORT).show()
            // In a real app, this would save a PDF
            // For now, simulate completion
            android.os.Handler(mainLooper).postDelayed({
                navigateHome()
            }, 1000)
        }
    }

    private fun navigateHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
