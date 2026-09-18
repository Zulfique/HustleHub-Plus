package com.hustlehub.app.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class TokenManager(context: Context) : AuthStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "hustlehub_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val jwtTokenKey = "jwt_token"
    private val userKey = "user_data"
    private val savedAtKey = "saved_at"
    private val ttlMs = 3600 * 1000L // 1 hour TTL

    private val gson = Gson()

    fun saveAuthData(token: String, user: User) {
        prefs.edit()
            .putString(jwtTokenKey, token)
            .putString(userKey, gson.toJson(user))
            .putLong(savedAtKey, System.currentTimeMillis())
            .apply()
    }

    fun getToken(): String? = prefs.getString(jwtTokenKey, null)

    fun getUser(): User? {
        val json = prefs.getString(userKey, null) ?: return null
        return try { gson.fromJson(json, User::class.java) }
        catch (_: Exception) { null }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null && getUser() != null
}