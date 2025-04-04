package com.example.integradora4to.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.response.GetBoxResponse
import com.example.integradora4to.models.request.response.UpdateBoxResponse
import com.example.integradora4to.repositories.SafeRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class SafeViewModel(private val repository: SafeRepository): ViewModel() {
    private val _boxData = MutableLiveData<GetBoxResponse?>()
    val boxData: LiveData<GetBoxResponse?> get() = _boxData


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _updatePinResponse = MutableLiveData<UpdateBoxResponse?>()
    val updatePinResponse: LiveData<UpdateBoxResponse?> get() = _updatePinResponse

    private val _navigateToLogin = MutableLiveData<Boolean?>()
    val navigateToLogin: LiveData<Boolean?> get() = _navigateToLogin


    fun getBoxByUserId(){
        viewModelScope.launch {
            try {
                val result = repository.getBoxByUserId()
                result.fold(
                    onSuccess = { response ->
                        _boxData.value = response
                        _errorMessage.value = null
                    },
                    onFailure = { error ->
                        if (error is HttpException) {
                            val errorBody = error.response()?.errorBody()?.string()
                            val errorMsg = errorBody?.let {
                                JSONObject(it).optString("msg", "Error al obtener la caja fuerte")
                            } ?: "Error retrieving safe: ${error.message}"

                            if (errorMsg.contains("Token expirado", ignoreCase = true)) {
                                _errorMessage.value = "Token expired. Please log in again."
                                _navigateToLogin.value = true
                            } else {
                                _errorMessage.value = errorMsg
                            }
                        } else {
                            _errorMessage.value = error.message
                        }
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
            }
        }
    }

    fun updatePinOfBox(vaultId: String, newPin: String) {
        viewModelScope.launch {
            try {
                val result = repository.updatePinOfSafe(vaultId, newPin)
                result.fold(
                    onSuccess = { response ->
                        _updatePinResponse.value = response
                        _errorMessage.value = null
                    },
                    onFailure = { error ->
                        if (error is HttpException) {
                            val errorBody = error.response()?.errorBody()?.string()
                            val errorMsg = errorBody?.let {
                                JSONObject(it).optString("msg", "Error al actualizar el PIN")
                            } ?: "Error retrieving safe: ${error.message}"

                            if (errorMsg.contains("Token expirado", ignoreCase = true)) {
                                _errorMessage.value = "Token expired. Please log in again."
                                _navigateToLogin.value = true
                            } else {
                                _errorMessage.value = errorMsg
                            }
                        } else {
                            _errorMessage.value = error.message
                        }
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
            }
        }
    }
}