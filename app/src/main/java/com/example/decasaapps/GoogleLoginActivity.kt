package com.example.decasaapps

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GoogleLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_google_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Account item click - navigasi ke UserLocFragment
        val accountItem = findViewById<LinearLayout>(R.id.accountItem)
        accountItem.setOnClickListener {
            navigateToUserLocFragment()
        }

        // Another account button click (optional)
        val anotherAccountButton = findViewById<LinearLayout>(R.id.anotherAccountButton)
        anotherAccountButton.setOnClickListener {
            // TODO: Handle use another account
            // Bisa navigasi ke fragment lain atau tampilkan dialog
            navigateToUserLocFragment()
        }
    }

    private fun navigateToUserLocFragment() {
        val fragment = UserLocFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(android.R.id.content, fragment)
            addToBackStack("google_login")
            commit()
        }
    }
}