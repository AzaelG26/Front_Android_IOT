package com.example.integradora4to

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        setupInputBorders()

        registerViewModel.registerResult.observe(this){ response ->
            Log.d("RegisterActivity", "Register result: $response")
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
            val username = binding.usernameEditTextRg.editText?.text.toString()
            val phone = binding.phoneEditTextRg.editText?.text.toString()
            val email = binding.emailEditTextRg.editText?.text.toString()
            val password = binding.passwordEditTextRg.editText?.text.toString()
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

    private fun setupInputBorders() {
        val colorFocused = ContextCompat.getColor(this, R.color.white)
        val colorDefault = ContextCompat.getColor(this, R.color.white)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_focused)
        )

        val colors = intArrayOf(
            colorFocused,
            colorDefault
        )

        val borderColorStateList = ColorStateList(states, colors)

        binding.usernameEditTextRg.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
        }

        binding.phoneEditTextRg.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
        }

        binding.emailEditTextRg.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
        }

        binding.passwordEditTextRg.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
            setEndIconTintList(ColorStateList.valueOf(colorDefault))
        }

        binding.usernameEditTextRg.editText?.setTextColor(colorDefault)
        binding.phoneEditTextRg.editText?.setTextColor(colorDefault)
        binding.emailEditTextRg.editText?.setTextColor(colorDefault)
        binding.passwordEditTextRg.editText?.setTextColor(colorDefault)
    }

}