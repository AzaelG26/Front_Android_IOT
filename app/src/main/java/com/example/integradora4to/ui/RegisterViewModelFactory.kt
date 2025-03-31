package com.example.integradora4to.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.integradora4to.repositories.RegisterRepository

class RegisterViewModelFactory: ViewModelProvider.Factory {
    private val repository = RegisterRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}