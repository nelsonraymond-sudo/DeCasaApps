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

            apiService.postLogin(user, pwd).enqueue(object : Callback<com.google.gson.JsonElement> {
                override fun onResponse(
                    call: Call<com.google.gson.JsonElement>,
                    response: Response<com.google.gson.JsonElement>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val rawJson = response.body().toString()
                        android.util.Log.d("LOGIN_RAW_JSON", rawJson)
                        
                        // Show Raw JSON to User for debugging
                        // android.widget.Toast.makeText(this@LoginActivity, "Raw: $rawJson", android.widget.Toast.LENGTH_LONG).show()

                        try {
                            val jsonObject = com.google.gson.JsonParser.parseString(rawJson).asJsonObject
                            
                            // 1. Extract Token
                            var token = when {
                                jsonObject.has("access_token") -> jsonObject.get("access_token").asString
                                jsonObject.has("token") -> jsonObject.get("token").asString
                                jsonObject.has("data") && jsonObject.get("data").isJsonObject && jsonObject.getAsJsonObject("data").has("token") -> 
                                    jsonObject.getAsJsonObject("data").get("token").asString
                                else -> null
                            }
                            
                            // 2. Extract Message
                            val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Login Successful"

                            // 3. Extract User Object (Try 'data' then 'user')
                            var userObj: com.google.gson.JsonObject? = null
                            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject) {
                                userObj = jsonObject.get("data").asJsonObject
                            } else if (jsonObject.has("user") && jsonObject.get("user").isJsonObject) {
                                userObj = jsonObject.get("user").asJsonObject
                            }
                            
                            // CRITICAL FIX: Check if the extracted object simply wraps the REAL user object
                            // The screenshot showed available keys: [user, token, token_type] matches exactly this case.
                            if (userObj != null && userObj.has("user") && userObj.get("user").isJsonObject) {
                                userObj = userObj.get("user").asJsonObject
                            }

                            if (userObj != null) {
                                // 4. Extract User Fields manually (Try all known variants)
                                val id = when {
                                    userObj.has("id_user") -> userObj.get("id_user").asString
                                    userObj.has("id") -> userObj.get("id").asString
                                    userObj.has("user_id") -> userObj.get("user_id").asString
                                    userObj.has("id_customer") -> userObj.get("id_customer").asString
                                    userObj.has("customer_id") -> userObj.get("customer_id").asString
                                    else -> null
                                }
                                val name = when {
                                    userObj.has("nm_user") -> userObj.get("nm_user").asString
                                    userObj.has("name") -> userObj.get("name").asString
                                    userObj.has("nm_customer") -> userObj.get("nm_customer").asString
                                    userObj.has("customer_name") -> userObj.get("customer_name").asString
                                    else -> ""
                                }
                                val email = when {
                                    userObj.has("email") -> userObj.get("email").asString
                                    else -> ""
                                }
                                val level = when {
                                    userObj.has("role") -> userObj.get("role").asString
                                    userObj.has("level") -> userObj.get("level").asString
                                    else -> "customer"
                                }

                                if (!id.isNullOrEmpty()) {
                                    // Save Session
                                    val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    editor.putString("KEY_NAME", name)
                                    editor.putString("KEY_EMAIL", email)
                                    editor.putString("KEY_ID", id)
                                    editor.putString("KEY_LEVEL", level)
                                    editor.putString("KEY_TOKEN", token)
                                    editor.putBoolean("KEY_IS_LOGGED_IN", true)
                                    editor.apply()

                                    // Update ApiClient token for immediately use
                                    com.example.decasaapps.network.ApiClient.token = token

                                    android.widget.Toast.makeText(this@LoginActivity, message, android.widget.Toast.LENGTH_SHORT).show()
                                    
                                    val intent = android.content.Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                     val availableKeys = userObj.keySet().joinToString(", ")
                                     android.widget.Toast.makeText(this@LoginActivity, "Login Failed: ID missing. Available keys: [$availableKeys]", android.widget.Toast.LENGTH_LONG).show()
                                }
                            } else {
                                android.widget.Toast.makeText(this@LoginActivity, "Login Failed: User data missing. Raw: $rawJson", android.widget.Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(this@LoginActivity, "Manual Parse Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                         val errorBody = response.errorBody()?.string()
                         android.widget.Toast.makeText(this@LoginActivity, "Login Failed: $errorBody", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<com.google.gson.JsonElement>, t: Throwable) {
                    val msg = "Error: ${t.message}"
                    android.widget.Toast.makeText(applicationContext, msg, android.widget.Toast.LENGTH_LONG).show()
                    android.util.Log.e("LOGIN_ERROR", "Failure: ${t.message}", t)
                }
            })
        }
    }
}