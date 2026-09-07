package com.hustlehub.app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hustlehub.app.api.ApiClient
import com.hustlehub.app.databinding.ActivityRegisterBinding
import com.hustlehub.app.model.RegisterRequest
import com.hustlehub.app.security.AuthStore
import com.hustlehub.app.security.TokenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authStore: AuthStore
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authStore = TokenManager(this)

        setupRoleSpinner()

        binding.btnRegister.setOnClickListener { handleRegister() }
        binding.tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("client", "freelancer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRole.adapter = adapter
    }

    private fun handleRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        val role = binding.spinnerRole.selectedItem?.toString() ?: "client"

        val validationError = validateInput(name, email, password, confirm)
        if (validationError != null) {
            showError(validationError)
            return
        }

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Creating account..."

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.register(
                    RegisterRequest(name, email, password, role)
                )
                authStore.saveAuthData(response.data.token, response.data.user)
                showError(null)
                Toast.makeText(this@RegisterActivity, "Account created!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } catch (e: Exception) {
                showError(ApiClient.parseErrorMessage(e))
            } finally {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = getString(R.string.sign_up)
            }
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirm: String
    ): String? {
        return when {
            TextUtils.isEmpty(name) -> getString(R.string.validation_name_required)
            TextUtils.isEmpty(email) -> getString(R.string.validation_email_required)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.validation_email_valid)
            password.length < 8 -> getString(R.string.validation_password_minimum)
            !password.any { it.isUpperCase() } -> getString(R.string.validation_password_uppercase)
            !password.any { it.isLowerCase() } -> getString(R.string.validation_password_lowercase)
            !password.any { it.isDigit() } -> getString(R.string.validation_password_digit)
            !password.any { "!@#$%^&*(),.?\":{}|<>".contains(it) } -> getString(R.string.validation_password_special)
            password != confirm -> getString(R.string.validation_password_mismatch)
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