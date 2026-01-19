package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReviewSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            ensureTokenInitialized()
            setContentView(R.layout.activity_review_summary)

            // Setup Back Button
            findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
                finish()
            }

            // Setup Change Payment Button
            findViewById<android.view.View>(R.id.btnChangePayment).setOnClickListener {
                finish()
            }

            // Setup Continue Button
            findViewById<android.view.View>(R.id.btnContinue).setOnClickListener {
                performBooking()
            }

            // Initial Data from Intent
            val extras = intent.extras
            if (extras != null) {
                android.util.Log.d("DEBUG_NAV", "ReviewSummaryActivity: Received ${extras.size()} extras")
            } else {
                android.util.Log.w("DEBUG_NAV", "ReviewSummaryActivity: NO EXTRAS RECEIVED!")
            }

            val propertyName = intent.getStringExtra("EXTRA_NAME")
            val propertyLocation = intent.getStringExtra("EXTRA_LOCATION")
            val imageUrl = intent.getStringExtra("EXTRA_IMAGE")
            val pMethodName = intent.getStringExtra("EXTRA_PAYMENT_METHOD_NAME") ?: "Cash"

            findViewById<TextView>(R.id.tvSummaryName).text = propertyName ?: ""
            findViewById<TextView>(R.id.tvSummaryLocation).text = propertyLocation ?: ""
            findViewById<TextView>(R.id.tvSummaryPaymentMethod).text = pMethodName
            
            val ivImage = findViewById<android.widget.ImageView>(R.id.ivSummaryImage)
            if (imageUrl != null && ivImage != null) {
                com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivImage)
            }

            android.util.Log.d("DEBUG_NAV", "ReviewSummaryActivity: Calling fetchPreview...")
            // Fetch Dynamic Summary
            fetchPreview()
        } catch (e: Exception) {
            android.util.Log.e("DEBUG_NAV", "CRASH in ReviewSummaryActivity.onCreate: ${e.message}", e)
            Toast.makeText(this, "Summary Crash: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchPreview() {
        val propertyId = intent.getStringExtra("EXTRA_ID") ?: return
        val checkin = intent.getStringExtra("EXTRA_CHECKIN") ?: return
        val checkout = intent.getStringExtra("EXTRA_CHECKOUT") ?: return

        val request = com.example.decasaapps.model.booking.PreviewRequest(propertyId, checkin, checkout)

        com.example.decasaapps.network.ApiClient.instance.getBookingPreview(request).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.booking.PreviewResponse> {
            override fun onResponse(call: retrofit2.Call<com.example.decasaapps.model.booking.PreviewResponse>, response: retrofit2.Response<com.example.decasaapps.model.booking.PreviewResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data ?: return
                    updateSummaryUI(data)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    val code = response.code()
                    
                    if (code == 401) {
                        Toast.makeText(this@ReviewSummaryActivity, "Session expired. Please re-login.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ReviewSummaryActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ReviewSummaryActivity, "Error $code: $errorBody", Toast.LENGTH_LONG).show()
                    }
                    android.util.Log.e("PREVIEW_ERROR", "Code: $code, Body: $errorBody")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.booking.PreviewResponse>, t: Throwable) {
                Toast.makeText(this@ReviewSummaryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSummaryUI(data: com.example.decasaapps.model.booking.PreviewData) {
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "Property"
        findViewById<TextView>(R.id.tvSummaryCategory)?.text = category

        // Booking Date is today (when user is reviewing)
        val today = java.util.Calendar.getInstance().time
        val sdf = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault())
        findViewById<TextView>(R.id.tvSummaryDate).text = sdf.format(today)

        findViewById<TextView>(R.id.tvSummaryCheckIn).text = formatDisplayDate(data.checkin)
        findViewById<TextView>(R.id.tvSummaryDuration).text = "${data.durasi} days"
        
        findViewById<TextView>(R.id.tvSummaryAmount).text = "Rp ${data.amount.toInt()}"
        
        // Per User Request: Tax deleted/zeroed
        findViewById<android.view.View>(R.id.llTaxRow)?.visibility = android.view.View.GONE 
        findViewById<TextView>(R.id.tvSummaryTax).text = "Rp 0"
        
        findViewById<TextView>(R.id.tvSummaryTotal).text = "Rp ${data.total.toInt()}"
        
        // Final Total Price
        findViewById<TextView>(R.id.tvSummaryPrice).text = "Rp ${data.total.toInt()}"
    }

    private fun performBooking() {
        val prefs = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("KEY_ID", null)
        val propertyId = intent.getStringExtra("EXTRA_ID")
        val pMethodId = intent.getStringExtra("EXTRA_PAYMENT_METHOD_ID") ?: "1"
        val checkin = intent.getStringExtra("EXTRA_CHECKIN") ?: ""
        val checkout = intent.getStringExtra("EXTRA_CHECKOUT") ?: ""

        if (userId == null) {
            Toast.makeText(this, "Session expired. Please re-login.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = com.example.decasaapps.model.booking.TransactionRequest(
            propertyId = propertyId ?: "",
            checkin = checkin ?: "",
            checkout = checkout ?: "",
            paymentMethodId = pMethodId ?: "1"
        )

        com.example.decasaapps.network.ApiClient.instance.storeTransaction(request).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.booking.TransactionResponse> {
            override fun onResponse(call: retrofit2.Call<com.example.decasaapps.model.booking.TransactionResponse>, response: retrofit2.Response<com.example.decasaapps.model.booking.TransactionResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val trxData = response.body()?.data
                    navigateToReceipt(trxData)
                } else {
                    val msg = response.errorBody()?.string() ?: "Booking failed!"
                    Toast.makeText(this@ReviewSummaryActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.booking.TransactionResponse>, t: Throwable) {
                Toast.makeText(this@ReviewSummaryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToReceipt(data: com.example.decasaapps.model.booking.TransactionData?) {
        val intent = Intent(this, EReceiptActivity::class.java)
        
        // Pass Property Info
        intent.putExtra("EXTRA_PROPERTY_NAME", this.intent.getStringExtra("EXTRA_NAME"))
        
        // Pass Transaction Data
        intent.putExtra("EXTRA_PRICE", "Rp ${data?.totalPrice}")
        intent.putExtra("EXTRA_DATE", System.currentTimeMillis())
        intent.putExtra("EXTRA_TRANSACTION_ID", data?.transactionId ?: "TRX-FAILED")
        
        // Pass Additional Details for Receipt
        intent.putExtra("EXTRA_CHECKIN", this.intent.getStringExtra("EXTRA_CHECKIN"))
        
        // Calculate Duration (Or pass if available)
        val duration = calculateDuration(
            this.intent.getStringExtra("EXTRA_CHECKIN"), 
            this.intent.getStringExtra("EXTRA_CHECKOUT")
        )
        intent.putExtra("EXTRA_DURATION", "$duration days")
        
        // Pass Financials (Assuming Data has breakdown or reusing preview values)
        // Since TransactionData simplified, we reuse Total Price
        intent.putExtra("EXTRA_AMOUNT", "Rp ${data?.totalPrice}")
        intent.putExtra("EXTRA_TAX", "$0") // Tax is 0 per user request
        intent.putExtra("EXTRA_TOTAL", "Rp ${data?.totalPrice}")

        startActivity(intent)
        finish()
    }

    private fun calculateDuration(inDate: String?, outDate: String?): Long {
        if (inDate == null || outDate == null) return 0
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date1 = sdf.parse(inDate)
            val date2 = sdf.parse(outDate)
            val diff = (date2?.time ?: 0) - (date1?.time ?: 0)
            java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
        } catch (e: Exception) { 0 }
    }

    private fun formatDisplayDate(dateStr: String): String {
        return try {
            val inputSdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputSdf = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault())
            val date = inputSdf.parse(dateStr)
            outputSdf.format(date)
        } catch (e: Exception) { dateStr }
    }

    private fun ensureTokenInitialized() {
        if (com.example.decasaapps.network.ApiClient.token == null) {
            val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            com.example.decasaapps.network.ApiClient.token = sharedPref.getString("KEY_TOKEN", null)
            android.util.Log.d("DEBUG_NAV", "ReviewSummaryActivity: Token initialized from Prefs: ${com.example.decasaapps.network.ApiClient.token != null}")
        }
    }
}
