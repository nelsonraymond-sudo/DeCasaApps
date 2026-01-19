package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ereceipt)

        // Retrieve Data from Intent
        val propertyName = intent.getStringExtra("EXTRA_PROPERTY_NAME") ?: "Mille Housing"
        val price = intent.getStringExtra("EXTRA_PRICE") ?: "Rp 0"
        val dateMillis = intent.getLongExtra("EXTRA_DATE", System.currentTimeMillis())
        val txId = intent.getStringExtra("EXTRA_TRANSACTION_ID") ?: "000000000000"
        val checkIn = intent.getStringExtra("EXTRA_CHECKIN") ?: "-"
        val duration = intent.getStringExtra("EXTRA_DURATION") ?: "0 days"
        val amount = intent.getStringExtra("EXTRA_AMOUNT") ?: price
        val tax = intent.getStringExtra("EXTRA_TAX") ?: "$0"
        val total = intent.getStringExtra("EXTRA_TOTAL") ?: price

        // Retrieve User Info from Preferences
        val prefs = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("KEY_NAME", "User Name") ?: "User Name"
        // Phone/Email might not be in prefs depending on Login response. Defaulting if missing.
        val userPhone = prefs.getString("KEY_EMAIL", "No Contact Info") ?: "No Contact Info" 

        // Setup Views
        // Receipt Header
        val tvBookingDate = findViewById<android.widget.TextView>(R.id.tvReceiptDate) // This is "Booking Date" in UI
        val tvCheckIn = findViewById<android.widget.TextView>(R.id.tvReceiptCheckIn) // Need to add ID in XML if missing
        val tvDuration = findViewById<android.widget.TextView>(R.id.tvReceiptDuration) // Need to add ID in XML if missing
        
        // Financials
        val tvAmount = findViewById<android.widget.TextView>(R.id.tvReceiptAmount)
        val tvTax = findViewById<android.widget.TextView>(R.id.tvReceiptTax) // Need to add ID in XML
        val tvTotal = findViewById<android.widget.TextView>(R.id.tvReceiptTotal)
        
        // User Info & Footer
        val tvName = findViewById<android.widget.TextView>(R.id.tvReceiptName) // Need to add ID in XML
        val tvPhone = findViewById<android.widget.TextView>(R.id.tvReceiptPhone) // Need to add ID in XML
        val tvTxId = findViewById<android.widget.TextView>(R.id.tvReceiptTransactionId)
        
        // Format Booking Date (Today)
        val sdf = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
        val dateString = sdf.format(java.util.Date(dateMillis))

        // Set Data
        tvBookingDate.text = dateString
        
        // Dynamic Views (Handling nulls if XML not updated yet, though I'll update XML next)
        tvCheckIn?.text = formatDisplayDate(checkIn)
        tvDuration?.text = duration
        
        tvAmount.text = amount
        tvTax?.text = tax
        tvTotal.text = total
        
        tvName?.text = userName
        tvPhone?.text = userPhone
        tvTxId.text = txId

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            navigateHome()
        }

        // Setup Download Button
        findViewById<android.view.View>(R.id.btnDownload).setOnClickListener {
            Toast.makeText(this, "Downloading E-Receipt...", Toast.LENGTH_SHORT).show()
            android.os.Handler(mainLooper).postDelayed({
                navigateHome()
            }, 1000)
        }
    }

    private fun formatDisplayDate(dateStr: String): String {
        return try {
            val inputSdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputSdf = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
            val date = inputSdf.parse(dateStr)
            outputSdf.format(date)
        } catch (e: Exception) { dateStr }
    }

    private fun navigateHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
