package com.example.integradora4to

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.integradora4to.ui.LoginViewModel
import com.example.integradora4to.ui.LoginViewModelFactory
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.integradora4to.databinding.ActivityDashboardBinding
import com.example.integradora4to.mqtt.MqttHelper
import com.example.integradora4to.network.ApiService
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.repositories.SafeRepository
import com.example.integradora4to.repositories.SensorRepository
import com.example.integradora4to.ui.SafeViewModel
import com.example.integradora4to.ui.SafeViewModelFactory
import com.example.integradora4to.ui.SensorViewModel
import com.example.integradora4to.ui.SensorViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val loginViewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(applicationContext)
    }
    val sensorRepository = SensorRepository()

    private val sensorViewModel: SensorViewModel by viewModels {
        SensorViewModelFactory(SensorRepository())
    }

    private val safeViewModel: SafeViewModel by viewModels {
        SafeViewModelFactory(SafeRepository(RetrofitClient.apiService, applicationContext))
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var welcomeText: TextView

    private lateinit var mqttHelper: MqttHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mqttHelper = MqttHelper("tcp://18.117.12.2:1883", "AndroidClient-${System.currentTimeMillis()}")
        mqttHelper.connect("admin", "password123",
            onConnected = {
                println("Conectado a MQTT")
            },
            onError = { e ->
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )



        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_dashboard)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.btnOpenSafe.visibility = View.INVISIBLE
        binding.btnRefresh.visibility = View.INVISIBLE
        binding.txtSensors.visibility = View.INVISIBLE
        binding.containerTemperature.visibility = View.INVISIBLE
        binding.containerHumidity.visibility = View.INVISIBLE
        binding.txtBoxNotFound.visibility = View.INVISIBLE

        val navigationView: NavigationView = binding.navView

        // Get user name
        welcomeText = findViewById(R.id.welcomeText)
        val username = loginViewModel.getUsername()

        welcomeText.text = if (!username.isNullOrEmpty()){
            "Hello $username 👋🏼"
        }
        else{
            "Welcome user 👋🏼"
        }

        navigationView.setNavigationItemSelectedListener(this)

        binding.content.findViewById<Button>(R.id.btn_open_safe).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.title))
                .setMessage(resources.getString(R.string.supporting_text))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                    dialog.dismiss()
                }
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    openSafeWithMQTT()
                }
                .show()
        }
        sensorViewModel.fetchSensorData()

        sensorViewModel.sensorData.observe(this){ data->
            data?.let {
                binding.txtDegrees.text = data.temperatura.toString()
                binding.txtHumidityValue.text = data.humedad.toString()
            }
        }

        binding.btnRefresh.setOnClickListener(){
            lifecycleScope.launch {
                sensorViewModel.fetchSensorData()
            }
        }
        safeViewModel.getBoxByUserId()

        safeViewModel.boxData.observe(this) { response ->
            Log.d("DashboardActivity", "boxData: $response")
            response?.let {
                val nicknames = it.box?.map { box -> box.nickname } ?: emptyList()
                if (nicknames.isNotEmpty()) {
                    binding.btnOpenSafe.visibility = View.VISIBLE
                    binding.btnRefresh.visibility = View.VISIBLE
                    binding.txtSensors.visibility = View.VISIBLE
                    binding.containerTemperature.visibility = View.VISIBLE
                    binding.containerHumidity.visibility = View.VISIBLE
                    binding.txtBoxNotFound.visibility = View.INVISIBLE


                    showSelectionDialog(nicknames)
                } else {
                    binding.btnOpenSafe.visibility = View.INVISIBLE
                    binding.btnRefresh.visibility = View.INVISIBLE
                    binding.txtSensors.visibility = View.INVISIBLE
                    binding.containerTemperature.visibility = View.INVISIBLE
                    binding.containerHumidity.visibility = View.INVISIBLE
                    binding.txtBoxNotFound.visibility = View.VISIBLE

                    Toast.makeText(this, "No se encontraron cajas fuertes", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Respuesta nula, deshabilitar el botón
                binding.btnOpenSafe.visibility = View.INVISIBLE
                binding.btnRefresh.visibility = View.INVISIBLE
                binding.txtSensors.visibility = View.INVISIBLE
                binding.containerTemperature.visibility = View.INVISIBLE
                binding.containerHumidity.visibility = View.INVISIBLE
                binding.txtBoxNotFound.visibility = View.VISIBLE

                Toast.makeText(this, "No se encontraron cajas fuertes", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showSelectionDialog(nicknames: List<String>) {
        if (nicknames.isEmpty()) {
            Toast.makeText(this, "No hay cajas fuertes registradas", Toast.LENGTH_SHORT).show()
            return
        }

        var selectedOption = 0 // Índice por defecto

        MaterialAlertDialogBuilder(this)
            .setTitle("Selecciona una caja fuerte")
            .setSingleChoiceItems(nicknames.toTypedArray(), selectedOption) { _, which ->
                selectedOption = which
            }
            .setPositiveButton("Confirmar") { _, _ ->
                Toast.makeText(this, "Seleccionaste: ${nicknames[selectedOption]}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun openSafeWithMQTT() {
        mqttHelper.publish("vault/remote/open", "OPEN")
        Toast.makeText(this, "Caja fuerte abierta", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.nav_your_safes -> goToYourSafes()
            R.id.nav_update_pin -> goToUpdatePin()
            R.id.nav_create_safe -> goToCreateSafe()
            R.id.nav_user -> goToYourAccount()
            R.id.logout -> {
                Toast.makeText(this, "Cerrando sesión...",Toast.LENGTH_SHORT ).show()
                logOut()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun goToCreateSafe(){
        startActivity(Intent(this, CreateSafeActivity::class.java))
        finish()
    }
    private fun goToYourAccount(){
        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }

    private fun goToUpdatePin(){
        startActivity(Intent(this, UpdatePinActivity::class.java))
        finish()
    }

    private fun goToYourSafes(){
        val goToYourSafes = Intent(this, TypeOfSafeActivity::class.java)
        startActivity(goToYourSafes)
        finish()
    }

    private fun logOut(){
        loginViewModel.logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
