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
import org.json.JSONObject

class LoginViewModel(context: Context): ViewModel() {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.logIn(LoginRequest(email, password))
                }

                if (response.tkn != null) {
                    saveToken(response.tkn, response.usr.username)
                    _loginResult.postValue(response)
                } else {
                    _errorMessage.postValue(response.msg)
                }

            } catch (e: Exception) {
                val errorBody = (e as? retrofit2.HttpException)?.response()?.errorBody()?.string()
                val errorMsg = errorBody?.let {
                    JSONObject(it).optString("msg", "Error en la autenticación")
                } ?: "Error en la autenticación: ${e.message}"

                _errorMessage.postValue(errorMsg)
            }
        }
    }


    private fun saveToken(token: String, username: String){
        sharedPreferences.edit().putString("auth_token", token).putString("username", username).apply()
    }

    fun getToken(): String?{
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUsername(): String?{
        return sharedPreferences.getString("username", null)
    }

    fun logOut() {
        sharedPreferences.edit().remove("auth_token").remove("username").apply()
    }
}