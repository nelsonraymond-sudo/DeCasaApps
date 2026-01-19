package com.example.decasaapps

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var rbCash: RadioButton
    private lateinit var rbBca: RadioButton
    private lateinit var rbMandiri: RadioButton
    
    private var paymentMethods = mutableListOf<com.example.decasaapps.model.booking.PaymentMethod>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureTokenInitialized()
        setContentView(R.layout.activity_payment_method)

        setupViews()
        fetchPaymentMethods()
    }

    private fun setupViews() {
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener { finish() }

        rbCash = findViewById(R.id.rbCash)
        rbBca = findViewById(R.id.rbBca)
        rbMandiri = findViewById(R.id.rbMandiri)

        findViewById<android.view.View>(R.id.cardCash).setOnClickListener { selectPaymentMethod(rbCash) }
        findViewById<android.view.View>(R.id.cardBca).setOnClickListener { selectPaymentMethod(rbBca) }
        findViewById<android.view.View>(R.id.cardMandiri).setOnClickListener { selectPaymentMethod(rbMandiri) }

        findViewById<android.view.View>(R.id.btnContinue).setOnClickListener {
            try {
                val selectedId = when {
                    rbCash.isChecked -> getMethodId("Cash")
                    rbBca.isChecked -> getMethodId("BCA")
                    rbMandiri.isChecked -> getMethodId("Mandiri")
                    else -> null
                }
                
                val selectedName = when {
                    rbCash.isChecked -> "Cash"
                    rbBca.isChecked -> "BCA"
                    rbMandiri.isChecked -> "Mandiri"
                    else -> null
                }

                android.util.Log.d("DEBUG_NAV", "PaymentMethodActivity: Button Clicked. Selected Method ID: $selectedId, Name: $selectedName")
                
                if (selectedId != null) {
                    val nextIntent = android.content.Intent(this, ReviewSummaryActivity::class.java)
                    val extras = intent.extras
                    if (extras != null) {
                        android.util.Log.d("DEBUG_NAV", "PaymentMethodActivity: Forwarding ${extras.size()} extras")
                        nextIntent.putExtras(extras)
                    } else {
                        android.util.Log.w("DEBUG_NAV", "PaymentMethodActivity: No extras found in intent!")
                    }
                    nextIntent.putExtra("EXTRA_PAYMENT_METHOD_ID", selectedId)
                    nextIntent.putExtra("EXTRA_PAYMENT_METHOD_NAME", selectedName)
                    
                    android.util.Log.d("DEBUG_NAV", "PaymentMethodActivity: Starting ReviewSummaryActivity...")
                    startActivity(nextIntent)
                } else {
                    Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_NAV", "CRASH in PaymentMethodActivity: ${e.message}", e)
                Toast.makeText(this, "Crash: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun fetchPaymentMethods() {
        com.example.decasaapps.network.ApiClient.instance.getPaymentMethods().enqueue(object : retrofit2.Callback<com.example.decasaapps.model.booking.PaymentMethodsResponse> {
            override fun onResponse(call: retrofit2.Call<com.example.decasaapps.model.booking.PaymentMethodsResponse>, response: retrofit2.Response<com.example.decasaapps.model.booking.PaymentMethodsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        paymentMethods.clear()
                        paymentMethods.addAll(it)
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(this@PaymentMethodActivity, "Session expired. Please re-login.", Toast.LENGTH_SHORT).show()
                    val intent = android.content.Intent(this@PaymentMethodActivity, LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.booking.PaymentMethodsResponse>, t: Throwable) {
                android.util.Log.e("PaymentMethod", "Error fetching methods", t)
            }
        })
    }

    private fun getMethodId(keyword: String): String? {
        val method = paymentMethods.find { it.name?.contains(keyword, ignoreCase = true) == true }
        if (method == null) {
            android.util.Log.w("PaymentMethod", "No backend match for $keyword, falling back to ID 1")
        }
        return method?.id ?: "1" // Default fallback for safety
    }

    private fun selectPaymentMethod(selectedRb: RadioButton) {
        rbCash.isChecked = false
        rbBca.isChecked = false
        rbMandiri.isChecked = false
        selectedRb.isChecked = true
    }

    private fun ensureTokenInitialized() {
        if (com.example.decasaapps.network.ApiClient.token == null) {
            val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            com.example.decasaapps.network.ApiClient.token = sharedPref.getString("KEY_TOKEN", null)
            android.util.Log.d("DEBUG_NAV", "PaymentMethodActivity: Token initialized from Prefs: ${com.example.decasaapps.network.ApiClient.token != null}")
        }
    }
}
