package com.example.decasaapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import android.widget.Button
import android.widget.ImageView
import com.example.decasaapps.client.Api
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.model.auth.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val user = emailInput.text.toString().trim()
            val pwd = passwordInput.text.toString().trim()

            // cek username not empty
            if (user.isEmpty()) {
                emailInput.error = "Email required"
                emailInput.requestFocus()
                return@setOnClickListener
            }
            // password not empty
            if (pwd.isEmpty()) {
                passwordInput.error = "Password required"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            // get response from REST API (web service)
            // get response from REST API (web service)
            val apiService: Api = RetrofitClient.instance.create(Api::class.java)

            apiService.postLogin(user, pwd).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val account = response.body()
                    if (response.isSuccessful && account != null) {

                        // Save User Session (Keep this existing logic for app functionality)
                        val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        val userData = account.user
                        
                        if (userData != null) {
                            editor.putString("KEY_NAME", userData.name)
                            editor.putString("KEY_EMAIL", userData.email)
                            editor.putString("KEY_ID", userData.id)
                            editor.putString("KEY_LEVEL", userData.level)
                            editor.putString("KEY_TOKEN", account.token)
                            editor.putBoolean("KEY_IS_LOGGED_IN", true)
                        }
                        editor.apply()

                        android.widget.Toast.makeText(this@LoginActivity, account.message, android.widget.Toast.LENGTH_SHORT).show()
                        
                        // Navigate to UserLocFragment (Replacing AccountActivity from screenshot to match app flow)
                        val fragment = UserLocFragment()
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.UserLocFragment, fragment)
                            addToBackStack(null)
                            commit()
                        }
                    } else {
                        android.widget.Toast.makeText(this@LoginActivity, "Login Failed: ${response.message()}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    android.widget.Toast.makeText(applicationContext, t.message, android.widget.Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}