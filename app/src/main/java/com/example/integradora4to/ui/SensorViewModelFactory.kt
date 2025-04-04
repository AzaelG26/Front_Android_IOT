package com.example.integradora4to.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.integradora4to.repositories.SensorRepository

class SensorViewModelFactory(private val repository: SensorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SensorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
