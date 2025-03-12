package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.integradora4to.ui.RegisterViewModel

class RegisterActivity: AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val inputUserName = findViewById<EditText>(R.id.username_edit_text_rg)
        val inputPhone = findViewById<EditText>(R.id.phone_edit_text_rg)
        val inputEmail = findViewById<EditText>(R.id.email_edit_text_rg)
        val inputPassword = findViewById<EditText>(R.id.password_edit_text_rg)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val backToLogin = findViewById<TextView>(R.id.backToLogin)

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

        btnRegister.setOnClickListener{
            val username = inputUserName.text.toString()
            val phone = inputPhone.text.toString()
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            registerViewModel.register(username, phone, email, password )
        }

        backToLogin.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}