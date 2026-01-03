package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ereceipt)

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
