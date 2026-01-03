package com.example.decasaapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_sign_up)
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

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val nameInput = findViewById<android.widget.EditText>(R.id.nameInput)
        val emailInput = findViewById<android.widget.EditText>(R.id.emailInput)
        val passwordInput = findViewById<android.widget.EditText>(R.id.passwordInput)
        val googleButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.googleButton)

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = com.example.decasaapps.model.auth.RegisterRequest(name, email, password)

            com.example.decasaapps.network.ApiClient.instance.register(request).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.auth.RegisterResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.decasaapps.model.auth.RegisterResponse>,
                    response: retrofit2.Response<com.example.decasaapps.model.auth.RegisterResponse>
                ) {
                    android.widget.Toast.makeText(this@SignUpActivity, "Registration Successful!", android.widget.Toast.LENGTH_SHORT).show()
                    
                    val fragment = VerificationFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.VerificationFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.auth.RegisterResponse>, t: Throwable) {
                    android.widget.Toast.makeText(this@SignUpActivity, "Registration Failed: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                     // Demo Fallback
                     val fragment = VerificationFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.VerificationFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            })
        }

        googleButton.setOnClickListener {
             // Simulate Google Login API Call
             com.example.decasaapps.network.ApiClient.instance.googleLogin("dummy_token").enqueue(object : retrofit2.Callback<com.example.decasaapps.model.auth.LoginResponse> {
                override fun onResponse(call: retrofit2.Call<com.example.decasaapps.model.auth.LoginResponse>, response: retrofit2.Response<com.example.decasaapps.model.auth.LoginResponse>) {
                     android.widget.Toast.makeText(this@SignUpActivity, "Google Login Successful!", android.widget.Toast.LENGTH_SHORT).show()
                     // Proceed to next screen
                     val fragment = VerificationFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.VerificationFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }
                
                override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.auth.LoginResponse>, t: Throwable) {
                     android.widget.Toast.makeText(this@SignUpActivity, "Google Login Failed (Demo)", android.widget.Toast.LENGTH_SHORT).show()
                      val fragment = VerificationFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.VerificationFragment, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }
             })
        }
    }
}