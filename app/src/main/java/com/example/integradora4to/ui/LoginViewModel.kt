package com.example.integradora4to.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.LoginRequest
import com.example.integradora4to.models.LoginResponse
import com.example.integradora4to.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(context: Context): ViewModel() {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String){
        viewModelScope.launch {
            try {
                val response: LoginResponse = withContext(Dispatchers.IO){
                    RetrofitClient.apiService.logIn(LoginRequest(email, password))
                }
                saveToken(response.tkn)
                _loginResult.postValue(response)
            }catch (e: Exception){
                _errorMessage.postValue("Error en la autenticación: ${e.message}")
            }
        }
    }

    private fun saveToken(token: String){
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String?{
        return sharedPreferences.getString("auth_token", null)
    }

    fun logOut() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
}