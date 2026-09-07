package com.hustlehub.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hustlehub.app.security.AuthStore
import com.hustlehub.app.security.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var authStore: AuthStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authStore = TokenManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (authStore.isLoggedIn()) {
                Intent(this, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            startActivity(destination)
            finish()
        }, 2000)
    }
}