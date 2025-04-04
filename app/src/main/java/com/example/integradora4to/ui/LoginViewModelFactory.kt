package com.example.integradora4to.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.integradora4to.repositories.LoginRepository

class LoginViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    private val repository = LoginRepository(context)


    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}