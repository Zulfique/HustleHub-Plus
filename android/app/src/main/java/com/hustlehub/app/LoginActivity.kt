package com.hustlehub.app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hustlehub.app.api.ApiClient
import com.hustlehub.app.databinding.ActivityLoginBinding
import com.hustlehub.app.model.LoginRequest
import com.hustlehub.app.security.AuthStore
import com.hustlehub.app.security.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authStore: AuthStore
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authStore = TokenManager(this)

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val validationError = validateInput(email, password)
        if (validationError != null) {
            showError(validationError)
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Signing in..."

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                authStore.saveAuthData(response.data.token, response.data.user)
                showError(null)
                Toast.makeText(this@LoginActivity, "Welcome, ${response.data.user.name}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } catch (e: Exception) {
                showError(ApiClient.parseErrorMessage(e))
            } finally {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.sign_in)
            }
        }
    }

    private fun validateInput(email: String, password: String): String? {
        return when {
            TextUtils.isEmpty(email) -> getString(R.string.validation_email_required)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.validation_email_valid)
            TextUtils.isEmpty(password) -> getString(R.string.validation_password_required)
            else -> null
        }
    }

    private fun showError(message: String?) {
        if (message == null) {
            binding.tvError.visibility = android.view.View.GONE
        } else {
            binding.tvError.text = message
            binding.tvError.visibility = android.view.View.VISIBLE
        }
    }
}