package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.decasaapps.model.booking.BookingRequest
import com.example.decasaapps.model.booking.BookingResponse
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.client.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_summary)

        // Retrieve Data
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Mille Housing"
        val location = intent.getStringExtra("EXTRA_LOCATION") ?: "Jakarta, Indonesia"
        val price = intent.getStringExtra("EXTRA_PRICE") ?: "$2,500/Year"
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")
        val paymentMethod = intent.getStringExtra("EXTRA_PAYMENT_METHOD") ?: "Cash"

        // Setup Views
        findViewById<TextView>(R.id.tvSummaryName).text = name
        findViewById<TextView>(R.id.tvSummaryLocation).text = location
        findViewById<TextView>(R.id.tvSummaryPrice).text = price
        findViewById<TextView>(R.id.tvSummaryPaymentMethod).text = paymentMethod
        
        val ivImage = findViewById<android.widget.ImageView>(R.id.ivSummaryImage)
        if (imageUrl != null) {
            com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivImage)
        }

        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Setup Change Payment Button
        findViewById<android.view.View>(R.id.btnChangePayment).setOnClickListener {
            finish() // Simply go back to Payment Activity
        }

        // Setup Continue Button (Simulate Payment Success)
        findViewById<android.view.View>(R.id.btnContinue).setOnClickListener {
            saveToHistory()
        }
    }

    private fun saveToHistory() {
        Toast.makeText(this, "Processing Payment...", Toast.LENGTH_SHORT).show()
        
        val prefs = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("KEY_ID", null)
        val propertyId = intent.getStringExtra("EXTRA_ID")
        val paymentMethod = intent.getStringExtra("EXTRA_PAYMENT_METHOD") ?: "Cash"
        val price = intent.getStringExtra("EXTRA_PRICE") ?: "$0"

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }
        if (propertyId.isNullOrEmpty()) {
            Toast.makeText(this, "Property data missing!", Toast.LENGTH_SHORT).show()
            // Just for testing, proceed if you want, but strictly checking is better.
            // context.startActivity(Intent(this, LoginActivity::class.java)) 
            return
        }

        val request = BookingRequest(
            userId = userId,
            propertyId = propertyId,
            paymentMethod = paymentMethod,
            totalPrice = price
        )

        val apiService = RetrofitClient.instance.create(Api::class.java)
        apiService.postBooking(request).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Toast.makeText(this@ReviewSummaryActivity, "Booking Successful!", Toast.LENGTH_SHORT).show()
                    
                    // Create receipt data
                    val receiptData = hashMapOf<String, Any>(
                         "propertyName" to (intent.getStringExtra("EXTRA_NAME") ?: ""),
                         "price" to price,
                         "date" to System.currentTimeMillis(),
                         "transactionId" to (body.transactionId ?: "TRX-${System.currentTimeMillis()}")
                    )
                    navigateToReceipt(receiptData)
                } else {
                    Toast.makeText(this@ReviewSummaryActivity, "Booking Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                Toast.makeText(this@ReviewSummaryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToReceipt(data: HashMap<String, Any>) {
        val intent = Intent(this, EReceiptActivity::class.java)
        // Pass data to Receipt
        intent.putExtra("EXTRA_PROPERTY_NAME", data["propertyName"] as String)
        intent.putExtra("EXTRA_PRICE", data["price"] as String)
        intent.putExtra("EXTRA_DATE", data["date"] as Long)
        intent.putExtra("EXTRA_TRANSACTION_ID", data["transactionId"] as String)
        
        startActivity(intent)
    }
}
