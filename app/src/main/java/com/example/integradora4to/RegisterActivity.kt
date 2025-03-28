package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.integradora4to.databinding.ActivityRegisterBinding
import com.example.integradora4to.ui.RegisterViewModel
import com.example.integradora4to.ui.RegisterViewModelFactory

class RegisterActivity: AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels() {
        RegisterViewModelFactory()
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel.registerResult.observe(this){ response ->
            if (response != null){
                Toast.makeText(this, "registration successful", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        registerViewModel.errorMessage.observe(this){ error ->
            if (error != null){
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnRegister.setOnClickListener{
            val username = binding.usernameEditTextRg.text.toString()
            val phone = binding.phoneEditTextRg.text.toString()
            val email = binding.emailEditTextRg.text.toString()
            val password = binding.passwordEditTextRg.text.toString()
            registerViewModel.register(username, phone, email, password )
        }

        binding.backToLogin.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.register) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}