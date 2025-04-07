package com.example.integradora4to.ui

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.CreateSafeRequest
import com.example.integradora4to.models.request.response.CreateSafeResponse
import com.example.integradora4to.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class CreateSafeViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("user_prefs", Application.MODE_PRIVATE)


    private val _createSafeResult = MutableLiveData<CreateSafeResponse?>()
    val createSafeResult: LiveData<CreateSafeResponse?> = _createSafeResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _navigateToLogin = MutableLiveData<Boolean?>()
    val navigateToLogin: LiveData<Boolean?> = _navigateToLogin



    fun createSafe(nickname: String, pin: Number){
        viewModelScope.launch {
            try {
                val token = getToken()
                if (token.isNullOrEmpty()) {
                    _errorMessage.postValue("Error: Token no encontrado")
                    return@launch
                }

                val response: CreateSafeResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.createSafe("Bearer $token", CreateSafeRequest(nickname, pin))
                }

                _createSafeResult.postValue(response)

            }catch (e: Exception) {
                val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()

                Log.d("CreateSafeViewModel", "Error Body: $errorBody")

                val errorMsg = errorBody?.let {
                    try {
                        val jsonError = JSONObject(it)
                        jsonError.optString("msg", "Error al crear la caja fuerte")
                    } catch (jsonException: JSONException) {
                        "Error desconocido: $it"
                    }
                } ?: "Error al crear la caja fuerte: ${e.message}"

                if (errorMsg.contains("Token expirado", ignoreCase = true)) {
                    _errorMessage.postValue("Token expirado. Por favor, inicie sesión de nuevo.")
                    _navigateToLogin.postValue(true)
                } else {
                    _errorMessage.postValue(errorMsg)
                }
            }
        }
    }

    private fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
}