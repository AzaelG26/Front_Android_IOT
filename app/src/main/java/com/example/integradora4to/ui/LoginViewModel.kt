package com.example.integradora4to.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.response.LoginResponse
import com.example.integradora4to.repositories.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository): ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { response ->
                _loginResult.postValue(response)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
        }
    }


    fun getToken(): String? = repository.getToken()

    fun getUsername(): String? = repository.getUsername()

    fun logOut() = repository.logOut()
}