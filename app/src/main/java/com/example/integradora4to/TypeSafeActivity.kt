package com.example.integradora4to

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.integradora4to.databinding.ActivityTypeOfSafeBinding


class TypeOfSafeActivity : AppCompatActivity(){
    private lateinit var binding: ActivityTypeOfSafeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeOfSafeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_t_of_s)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // Evita duplicados
        startActivity(intent)
        finish() // Se cierra CreateSafeActivity
    }
}