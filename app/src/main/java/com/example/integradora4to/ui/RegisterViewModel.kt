package com.example.integradora4to.ui

import RegisterRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: RegisterRepository): ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun register (username: String, phone: String, email:String, password: String ){
        viewModelScope.launch {
            val result = repository.register(username, phone, email, password)
            result.onSuccess { response ->
                _registerResult.postValue(response)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
        }
    }
}