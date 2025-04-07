package com.example.integradora4to

import android.content.ClipData.Item
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import com.example.integradora4to.databinding.ActivityMainBinding
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory

class MainActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels(){
        LoginViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityMainBinding // binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val inputEmail = findViewById<EditText>(R.id.email_edit_text)
        // val inputPassword = findViewById<EditText>(R.id.password_edit_text)
        // val buttonLogin = findViewById<Button>(R.id.btnLog_in)
        // val message = findViewById<TextView>(R.id.messages)
        // val signUp = findViewById<TextView>(R.id.signUp)


        val savedToken = loginViewModel.getToken()
        if (savedToken != null){
            Toast.makeText(this, "User is already authenticated", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        setupInputBorders()

        loginViewModel.loginResult.observe(this){ response ->
            if(response!=null){
                Toast.makeText(this,  response.msg , Toast.LENGTH_LONG).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.errorMessage.observe(this){ error ->
            if (error != null){
                binding.messages.text = error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }


        binding.btnLogIn.setOnClickListener(){
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginViewModel.login(email, password)
        }

        binding.signUp.setOnClickListener(){
            val goSignUp = Intent(this, RegisterActivity::class.java)
            startActivity(goSignUp)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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

        binding.emailEditTextLayout.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
        }

        binding.passwordEditTextLayout.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
            setEndIconTintList(ColorStateList.valueOf(colorDefault))
        }

        binding.emailEditText.setTextColor(colorDefault)
        binding.passwordEditText.setTextColor(colorDefault)
    }
}