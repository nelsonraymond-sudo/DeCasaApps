package com.example.decasaapps.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "user_token"
        const val KEY_IS_LOGIN = "is_login"
        const val KEY_NAME = "user_name"
    }

    // Simpan token dan nama saat login berhasil
    fun saveSession(token: String, name: String) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_NAME, name)
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.apply()
    }

    // Cek status login
    fun isLogin(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    // Ambil token untuk dipakai di header request nanti
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // Logout
    fun logout() {
        prefs.edit().clear().apply()
    }
}