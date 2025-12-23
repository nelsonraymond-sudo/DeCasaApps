package com.example.decasaapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import android.widget.Button
import android.widget.ImageView

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge (dari androidx.core.view)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Tombol back arrow
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Kembali ke Activity sebelumnya
        }

        val loginButton = findViewById<Button>(R.id.login)

        loginButton.setOnClickListener {
            val fragment = UserLocFragment()

            // supportFragmentManager sudah otomatis tersedia
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.UserLocFragment, fragment)
                addToBackStack(null)
                commit()
            }
        }
    }
}