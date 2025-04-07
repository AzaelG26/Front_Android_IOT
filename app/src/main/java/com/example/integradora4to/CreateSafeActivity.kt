package com.example.integradora4to

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.integradora4to.databinding.ActivityCreateSafeBinding
import com.example.integradora4to.databinding.ActivityMainBinding
import com.example.integradora4to.ui.CreateSafeViewModel
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory

class CreateSafeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCreateSafeBinding
    private val createSafeViewModel: CreateSafeViewModel by viewModels();
    private val loginViewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSafeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_c_s)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupInputBorders()

        binding.btnCS.setOnClickListener {
            val nickname = binding.nicknameCS.text.toString().trim()
            val pinEditText = binding.pinCS

            if (nickname.isEmpty()) {
                binding.errorCS.text = "Nickname is required"
            } else {
                binding.errorCS.text = ""

                val pinString = pinEditText.text.toString().trim()
                if (pinString.isNotEmpty()) {
                    try {
                        val pin = pinString.toInt()
                        createSafeViewModel.createSafe(nickname, pin)
                    } catch (e: NumberFormatException) {
                        binding.errorCS.text = "Invalid PIN format"
                    }
                } else {
                    binding.errorCS.text = "PIN is required"
                }
            }
        }

        createSafeViewModel.createSafeResult.observe(this){ response ->
            if (response != null){
                Toast.makeText(this, "Safe created successfully!", Toast.LENGTH_SHORT).show()
                goToDashboard()
            }
        }

        createSafeViewModel.errorMessage.observe(this){ error ->
            error?.let {
                binding.errorCS.text = it
            }
        }


        createSafeViewModel.navigateToLogin.observe(this){ navigate ->
            if (navigate == true){
                logOut()
            }
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

        binding.nicknameLayout.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
        }

        binding.pinLayout.apply {
            setBoxStrokeColorStateList(borderColorStateList)
            boxStrokeWidth = 1
            boxStrokeWidthFocused = 2
            hintTextColor = ColorStateList.valueOf(colorDefault)
            setEndIconTintList(ColorStateList.valueOf(colorDefault))
        }

        binding.nicknameCS.setTextColor(colorDefault)
        binding.pinCS.setTextColor(colorDefault)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if (item.itemId == android.R.id.home){
            goToDashboard()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun logOut() {
        loginViewModel.logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}