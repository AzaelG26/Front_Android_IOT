package com.example.integradora4to

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
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
import com.example.integradora4to.databinding.ActivityDashboardBinding
import com.example.integradora4to.mqtt.MqttHelper
import com.example.integradora4to.ui.CreateSafeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val loginViewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(applicationContext)
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

        mqttHelper = MqttHelper("tcp://3.14.129.17:1883", "AndroidClient-${System.currentTimeMillis()}")
        mqttHelper.connect("admin", "password123",
            onConnected = {
                println("Conectado a MQTT")
            },
            onError = { e ->
                Toast.makeText(this, "Error al conectar MQTT: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )



        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_dashboard)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



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

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
            R.id.nav_item_two -> Toast.makeText(this, "item 2", Toast.LENGTH_SHORT).show()
            R.id.nav_update_pin -> goToUpdatePin()
            R.id.nav_create_safe -> goToCreateSafe()
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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Borra historial
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
