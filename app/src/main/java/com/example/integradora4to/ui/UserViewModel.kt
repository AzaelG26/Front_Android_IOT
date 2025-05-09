package com.example.integradora4to.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.UpdateUserRequest
import com.example.integradora4to.models.request.UpdateUserResponse
import com.example.integradora4to.models.request.response.User
import com.example.integradora4to.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository, context: Context): ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    private val _updateResult = MutableLiveData<Result<UpdateUserResponse>>()
    val updateResult: LiveData<Result<UpdateUserResponse>> get() = _updateResult

    private val _navigateToLogin = MutableLiveData<Boolean?>()
    val navigateToLogin: LiveData<Boolean?> get() = _navigateToLogin


    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val userId: String? = sharedPreferences.getString("user_id", null)

    fun getUser(){
        viewModelScope.launch {
            if (userId != null) {
                val result = userRepository.getUser(userId)
                result.onSuccess { user ->
                    _user.postValue(user)
                }.onFailure { error ->
                    _errorMessage.postValue(error.message)
                }
            }else{
                _errorMessage.postValue("User ID not found in SharedPreferences.")
            }
        }
    }

    fun updateUser(token: String, username: String?, phone: String?, email: String?, password: String?, passwordConfirmation: String?){
        val request = UpdateUserRequest(username, phone, email, password, passwordConfirmation )
        viewModelScope.launch {
            val result = userRepository.updateUser(token, request)
            _updateResult.postValue(result)

            result.onFailure { error ->
                if (error.message == "Token expirado") {
                    _errorMessage.postValue("Token expirado. Por favor, inicie sesión nuevamente.")
                    _navigateToLogin.postValue(true)
                } else {
                    _errorMessage.postValue(error.message)
                }
            }
        }
    }
}