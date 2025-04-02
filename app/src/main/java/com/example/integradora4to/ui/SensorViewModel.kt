package com.example.integradora4to.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora4to.models.request.response.SensorDataResponse
import com.example.integradora4to.repositories.SensorRepository
import kotlinx.coroutines.launch

class SensorViewModel(private val repository: SensorRepository): ViewModel() {
    private val _sensorData = MutableLiveData<SensorDataResponse?>()
    val sensorData: LiveData<SensorDataResponse?> = _sensorData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchSensorData(){
        viewModelScope.launch {
            val result = repository.getSensorData()
            result.onSuccess { data ->
                _sensorData.postValue(data)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
        }
    }
}