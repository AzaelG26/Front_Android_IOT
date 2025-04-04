package com.example.integradora4to.repositories

import android.util.Log
import com.example.integradora4to.models.request.response.SensorDataResponse
import com.example.integradora4to.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SensorRepository{
    suspend fun getSensorData(): Result<SensorDataResponse>{
        return try {
            val response: SensorDataResponse = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getSensorData()
            }
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMsg = errorBody?.let {
                JSONObject(it).optString("msg", "Error al obtener datos del sensor")
            } ?: "Error al obtener datos del sensor"

            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Log.e("SensorError", e.message.orEmpty())
            Result.failure(Exception("Error en la petición: ${e.message}"))
        }
    }

}