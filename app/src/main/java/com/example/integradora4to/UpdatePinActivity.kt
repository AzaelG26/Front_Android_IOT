package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.integradora4to.databinding.ActivityUpdatePinBinding
import com.example.integradora4to.models.request.response.GetBoxResponse
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.repositories.SafeRepository
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory
import com.example.integradora4to.ui.SafeViewModel
import com.example.integradora4to.ui.SafeViewModelFactory

class UpdatePinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePinBinding
    private val safeViewModel: SafeViewModel by viewModels {
        SafeViewModelFactory(SafeRepository(RetrofitClient.apiService, applicationContext))
    }
    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_up_spin)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        safeViewModel.navigateToLogin.observe(this) { navigate ->
            if (navigate == true) {
                logOut()
            }
        }

        safeViewModel.boxData.observe(this) { response ->
            response?.let { updateSpinner(it) }
        }

        safeViewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        safeViewModel.getBoxByUserId()


        safeViewModel.updatePinResponse.observe(this){response->
            response?.let {
                Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdate.setOnClickListener() {
            val selectedPosition = binding.spinnerVaults.selectedItemPosition
            val selectedBox = safeViewModel.boxData.value?.box?.get(selectedPosition)
            if (selectedBox != null) {
                val vaultId = selectedBox.id
                val newPin = binding.etNewPin.text.toString()
                if (newPin.isNotEmpty()) {
                    safeViewModel.updatePinOfBox(vaultId, newPin)
                } else {
                    Toast.makeText(this, "Enter a new PIN", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select one of your safes", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            goToDashboard()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateSpinner(response: GetBoxResponse) {
        val boxList = response.box ?: emptyList()
        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, boxList.map { it.nickname })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVaults.adapter = adapter
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // Evita duplicados
        startActivity(intent)
        finish() // Se cierra CreateSafeActivity
    }
    private fun logOut() {
        loginViewModel.logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}