package com.example.decasaapps

import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.decasaapps.client.Api
import com.example.decasaapps.client.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class EditProfile : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDob: EditText
    private lateinit var etPhone: EditText
    private lateinit var etGender: EditText
    private lateinit var ivProfile: ImageView
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivProfile.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etName = findViewById(R.id.etName)
        etDob = findViewById(R.id.etDob)
        etPhone = findViewById(R.id.etPhone)
        etGender = findViewById(R.id.etGender)
        ivProfile = findViewById(R.id.ivProfile)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnChangePhoto).setOnClickListener {
            getContent.launch("image/*")
        }
        
        findViewById<android.widget.Button>(R.id.btnUpdate).setOnClickListener {
            updateProfile()
        }

        ensureTokenInitialized()
        fetchProfile()
    }

    private fun ensureTokenInitialized() {
        if (com.example.decasaapps.network.ApiClient.token == null) {
            val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            com.example.decasaapps.network.ApiClient.token = sharedPref.getString("KEY_TOKEN", null)
        }
    }

    private fun fetchProfile() {
        val api = RetrofitClient.instance.create(Api::class.java)
        api.getProfile().enqueue(object : Callback<com.google.gson.JsonElement> {
            override fun onResponse(call: Call<com.google.gson.JsonElement>, response: Response<com.google.gson.JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val json = response.body()!!.asJsonObject
                        if (json.has("success") && json.get("success").asBoolean) {
                            val data = json.get("data").asJsonObject
                            
                            val name = if (data.has("nm_user") && !data.get("nm_user").isJsonNull) data.get("nm_user").asString else ""
                            val dob = if (data.has("birth_date") && !data.get("birth_date").isJsonNull) data.get("birth_date").asString else ""
                            val phone = if (data.has("no_hp") && !data.get("no_hp").isJsonNull) data.get("no_hp").asString else ""
                            val gender = if (data.has("gender") && !data.get("gender").isJsonNull) data.get("gender").asString else ""

                            etName.setText(name)
                            etDob.setText(dob)
                            etPhone.setText(phone)
                            etGender.setText(gender)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("EDIT_PROFILE", "Parse error", e)
                    }
                }
            }
            override fun onFailure(call: Call<com.google.gson.JsonElement>, t: Throwable) {
                Toast.makeText(this@EditProfile, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val name = etName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Get email from session since it's required by backend
        val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val emailStr = sharedPref.getString("KEY_EMAIL", "") ?: ""
        val emailBody = emailStr.toRequestBody("text/plain".toMediaTypeOrNull())

        val phone = etPhone.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val gender = etGender.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val dob = etDob.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        var photoBody: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val fileDir = applicationContext.filesDir
            val file = File(fileDir, "profile_image.png")
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream!!.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            photoBody = MultipartBody.Part.createFormData("photo", file.name, reqFile)
        }

        val api = RetrofitClient.instance.create(Api::class.java)
        api.updateProfile(name, emailBody, phone, gender, dob, null, photoBody).enqueue(object : Callback<com.google.gson.JsonElement> {
            override fun onResponse(call: Call<com.google.gson.JsonElement>, response: Response<com.google.gson.JsonElement>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfile, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProfile, "Update Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<com.google.gson.JsonElement>, t: Throwable) {
                Toast.makeText(this@EditProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}