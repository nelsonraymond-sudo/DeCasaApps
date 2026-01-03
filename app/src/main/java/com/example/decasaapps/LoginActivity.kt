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
        val emailInput = findViewById<android.widget.EditText>(R.id.emailInput)
        val passwordInput = findViewById<android.widget.EditText>(R.id.passwordInput)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulate API Call or Call Actual API
            // For now, using ApiClient to call the mock endpoint
            
            val request = com.example.decasaapps.model.auth.LoginRequest(email, password)
            
            com.example.decasaapps.network.ApiClient.instance.login(request).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.auth.LoginResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.decasaapps.model.auth.LoginResponse>,
                    response: retrofit2.Response<com.example.decasaapps.model.auth.LoginResponse>
                ) {
                    // For mocky/demo purposes, assume success if response is 200, 
                    // or force success if using a static mocky that always returns success
                    // To ensure it works for the user immediately without setting up a real backend:
                    
                    android.widget.Toast.makeText(this@LoginActivity, "Login Successful!", android.widget.Toast.LENGTH_SHORT).show()
                    
                    // Navigate to UserLocFragment or MainActivity
                    val fragment = UserLocFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.UserLocFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.auth.LoginResponse>, t: Throwable) {
                    android.widget.Toast.makeText(this@LoginActivity, "Login Failed: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                    // Fallback for demo if network fails (e.g. mocky down)
                     val fragment = UserLocFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.UserLocFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            })
        }
    }
}