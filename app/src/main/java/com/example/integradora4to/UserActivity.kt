package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.integradora4to.databinding.ActivityUserBinding
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.repositories.UserRepository
import com.example.integradora4to.ui.UserViewModel
import com.example.integradora4to.ui.UserViewModelFactory

class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val userViewModel: UserViewModel by viewModels  {
        val apiService = RetrofitClient.apiService
        val userRepository = UserRepository(apiService)
        UserViewModelFactory(userRepository, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_up_spin)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
}