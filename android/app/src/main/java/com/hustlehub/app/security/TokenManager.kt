package com.hustlehub.app.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

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

    fun saveAuthData(token: String, userJson: String) {
        prefs.edit()
            .putString(jwtTokenKey, token)
            .putString(userKey, userJson)
            .putLong(savedAtKey, System.currentTimeMillis())
            .apply()
    }

    fun getToken(): String? = prefs.getString(jwtTokenKey, null)

    fun getUserJson(): String? = prefs.getString(userKey, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        val token = getToken() ?: return false

        // Check storage age fallback for tokens without valid expiry claim
        val savedAt = prefs.getLong(savedAtKey, 0L)
        val storageAge = System.currentTimeMillis() - savedAt
        if (savedAt > 0 && storageAge > ttlMs) {
            // Token stored for more than 1 hour without expiry check - reject
            clear()
            return false
        }

        // Decode JWT to check actual exp claim
        try {
            val parts = token.split('.')
            if (parts.size != 3) {
                clear()
                return false
            }
            val payload = parts[1]
            val base64Payload = if (payload.startsWith("~")) payload.drop(1) else payload
            // Add padding if needed
            val padded = base64Payload + "=".repeat((4 - base64Payload.length % 4) % 4)
            val decodedJson = java.util.Base64.getDecoder().decode(padded)
            val jsonStr = String(decodedJson)
            val expIndex = jsonStr.indexOf("\"exp\"")
            if (expIndex >= 0) {
                val expValueStart = jsonStr.indexOf(':', expIndex) + 1
                val expValueEnd = jsonStr.indexOf(',', expValueStart)
                val expStr = if (expValueEnd >= 0) {
                    jsonStr.substring(expValueStart, expValueEnd).trim()
                } else {
                    val braceEnd = jsonStr.indexOf('}', expValueStart)
                    jsonStr.substring(expValueStart, braceEnd).trim()
                }
                val expLong = expStr.toLong()
                val nowLong = System.currentTimeMillis() / 1000L
                if (expLong <= nowLong) {
                    // Token is expired
                    clear()
                    return false
                }
            } else {
                // No exp claim found - use storage age fallback
                if (storageAge > ttlMs) {
                    clear()
                    return false
                }
            }
        } catch (e: Exception) {
            // If we can't decode the token, fall back to storage age check
            if (savedAt > 0 && storageAge > ttlMs) {
                clear()
                return false
            }
        }

        return true
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER = "user_data"
        private const val KEY_SAVED_AT = "saved_at"
        private const val TOKEN_TTL_MS = 3600 * 1000L
    }
}