package com.example.integradora4to

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.integradora4to.databinding.ActivityUserBinding
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.repositories.UserRepository
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory
import com.example.integradora4to.ui.UserViewModel
import com.example.integradora4to.ui.UserViewModelFactory

class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val userViewModel: UserViewModel by viewModels  {
        val apiService = RetrofitClient.apiService
        val userRepository = UserRepository(apiService)
        UserViewModelFactory(userRepository, this)
    }
    private val loginViewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_up_spin)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnUpdate.setOnClickListener {
            val token = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("auth_token", null)
            if (token != null) {
                val username = binding.etUsername.text.toString().takeIf { it.isNotEmpty() }
                val phone = binding.etPhone.text.toString().takeIf { it.isNotEmpty() }
                val email = binding.etEmail.text.toString().takeIf { it.isNotEmpty() }
                val password = binding.etPasswordUser.text.toString().takeIf { it.isNotEmpty() }
                val passwordConfirmation = binding.etPasswordConfirmationUser.text.toString().takeIf { it.isNotEmpty() }

                userViewModel.updateUser(token,username, phone, email, password, passwordConfirmation)
            } else {
                Toast.makeText(this, "No se encontró el token de autenticación", Toast.LENGTH_SHORT).show()
            }

        }
        userViewModel.updateResult.observe(this) { response ->
            response?.let {
                it.onSuccess { response ->
                    binding.etPasswordUser.text?.clear()
                    binding.etPasswordConfirmationUser.text?.clear()
                    Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
                it.onFailure { error ->
                    Toast.makeText(
                        this,
                        "Error al actualizar usuario: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        userViewModel.navigateToLogin.observe(this) { navigate ->
            if (navigate == true){
                logOut()
            }
        }


        userViewModel.user.observe(this) { user ->
            if (user != null) { // Check if user is not null
                binding.etUsername.setText(user.username)
                binding.etPhone.setText(user.phone)
                binding.etEmail.setText(user.email)
            }
        }
        userViewModel.getUser()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            goToDashboard()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // Evita duplicados
        startActivity(intent)
        finish() // Se cierra CreateSafeActivity
    }
    private fun logOut() {
        loginViewModel.logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Borra historial
        startActivity(intent)
        finish()
    }
}