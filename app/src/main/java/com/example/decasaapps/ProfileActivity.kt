package com.example.decasaapps

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge (dari androidx.core.view)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_profile)

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


        val profileButton = findViewById<Button>(R.id.completeProfileButton)

        profileButton.setOnClickListener {
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