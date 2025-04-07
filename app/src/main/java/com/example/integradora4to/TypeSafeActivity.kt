package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.integradora4to.adapters.BoxListAdapter
import com.example.integradora4to.databinding.ActivityTypeOfSafeBinding
import com.example.integradora4to.models.request.response.Box
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.repositories.SafeRepository
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory
import com.example.integradora4to.ui.SafeViewModel
import com.example.integradora4to.ui.SafeViewModelFactory


class TypeOfSafeActivity : AppCompatActivity(){
    private lateinit var binding: ActivityTypeOfSafeBinding
    private val safeViewModel: SafeViewModel by viewModels {
        SafeViewModelFactory(SafeRepository(RetrofitClient.apiService, applicationContext))
    }
    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(applicationContext) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BoxListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeOfSafeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_t_of_s)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        recyclerView = findViewById(R.id.recyclerViewBoxes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BoxListAdapter(emptyList())
        recyclerView.adapter = adapter

        safeViewModel.navigateToLogin.observe(this) { navigate ->
            if (navigate == true) {
                logOut()
            }
        }

        safeViewModel.boxData.observe(this) { response ->
            response?.let { updateList(it.box ?: emptyList()) }
        }

        safeViewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        safeViewModel.getBoxByUserId()
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

    private fun updateList(boxes: List<Box>) {
        adapter.updateData(boxes)
    }

    private fun logOut() {
        loginViewModel.logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}