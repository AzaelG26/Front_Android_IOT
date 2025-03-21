package com.example.integradora4to.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.RegisterRequest
import com.example.integradora4to.models.RegisterResponse
import com.example.integradora4to.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RegisterViewModel: ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun register (username: String, phone: String, email:String, password: String ){
        viewModelScope.launch {
            try {
                val response: RegisterResponse = withContext(Dispatchers.IO){
                    RetrofitClient.apiService.register(RegisterRequest(username, phone, email, password))
                }
                _registerResult.postValue(response)

            }catch (e: retrofit2.HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = errorBody ?.let {
                    JSONObject(it).optString("msg", "Error en el registro")
                } ?: "Error en el registro"

                _errorMessage.postValue(errorMsg)
            }
            catch (e: Exception){
                _errorMessage.postValue("Error en el registro: ${e.message}")
                Log.e("RegisterError", e.message.orEmpty())
            }
        }
    }
}