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
        val phoneInput = findViewById<android.widget.EditText>(R.id.phoneInput)
        val passwordInput = findViewById<android.widget.EditText>(R.id.passwordInput)
        val googleButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.googleButton)

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            val password = passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = com.example.decasaapps.model.auth.RegisterRequest(
                name = name,
                email = email,
                no_hp = phone,
                password = password,
                passwordConfirmation = password
            )
            val apiService = com.example.decasaapps.client.RetrofitClient.instance.create(com.example.decasaapps.client.Api::class.java)

            apiService.register(request).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.auth.RegisterResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.decasaapps.model.auth.RegisterResponse>,
                    response: retrofit2.Response<com.example.decasaapps.model.auth.RegisterResponse>
                ) {
                    val regResponse = response.body()
                    if (response.isSuccessful) {
                        android.widget.Toast.makeText(this@SignUpActivity, "Registration Successful!", android.widget.Toast.LENGTH_SHORT).show()
                        
                        // Navigate to Login or Verification
                        // Assuming VerificationFragment is the next step
                        val fragment = VerificationFragment()
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.VerificationFragment, fragment)
                            addToBackStack(null)
                            commit()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            val jsonObject = org.json.JSONObject(errorBody ?: "")
                            jsonObject.optString("message", "Unknown Error")
                        } catch (e: Exception) {
                            "Error: ${response.code()}"
                        }
                        android.widget.Toast.makeText(this@SignUpActivity, "Registration Failed: $errorMessage", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.auth.RegisterResponse>, t: Throwable) {
                    android.widget.Toast.makeText(this@SignUpActivity, "Registration Failed: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
        }

        googleButton.setOnClickListener {
             android.widget.Toast.makeText(this@SignUpActivity, "Google Login not yet connected to Laravel API", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}