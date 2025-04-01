package com.example.integradora4to.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.integradora4to.repositories.SafeRepository

class SafeViewModelFactory(private val repository: SafeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafeViewModel::class.java)) {
            return SafeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}