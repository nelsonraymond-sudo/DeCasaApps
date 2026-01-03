package com.example.decasaapps

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var rbCash: RadioButton
    private lateinit var rbCreditCard: RadioButton
    private lateinit var rbDana: RadioButton
    private lateinit var rbQris: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        setupViews()
    }

    private fun setupViews() {
        // Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Radio Buttons
        rbCash = findViewById(R.id.rbCash)
        rbCreditCard = findViewById(R.id.rbCreditCard)
        rbDana = findViewById(R.id.rbDana)
        rbQris = findViewById(R.id.rbQris)

        // Click Listeners for Rows
        findViewById<android.view.View>(R.id.cardCash).setOnClickListener { selectPaymentMethod(rbCash) }
        findViewById<android.view.View>(R.id.cardCreditCard).setOnClickListener { selectPaymentMethod(rbCreditCard) }
        findViewById<android.view.View>(R.id.cardDana).setOnClickListener { selectPaymentMethod(rbDana) }
        findViewById<android.view.View>(R.id.cardQris).setOnClickListener { selectPaymentMethod(rbQris) }

        // Continue Button
        findViewById<android.view.View>(R.id.btnContinue).setOnClickListener {
            val selected = when {
                rbCash.isChecked -> "Cash"
                rbCreditCard.isChecked -> "Credit/Debit Card"
                rbDana.isChecked -> "Dana"
                rbQris.isChecked -> "QRIS"
                else -> null
            }

        if (selected != null) {
                // Proceed to Review Summary
                val intent = android.content.Intent(this, ReviewSummaryActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectPaymentMethod(selectedRb: RadioButton) {
        rbCash.isChecked = false
        rbCreditCard.isChecked = false
        rbDana.isChecked = false
        rbQris.isChecked = false

        selectedRb.isChecked = true
    }
}
