package com.example.integradora4to.repositories

import android.util.Log
import com.example.integradora4to.models.request.response.SensorDataResponse
import com.example.integradora4to.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SensorRepository {
    suspend fun getSensorData(): Result<SensorDataResponse> {
        return try {
            val response: SensorDataResponse = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getSensorData()
            }
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorCode = e.response()?.code()
            Log.e("SensorError", "Error HTTP $errorCode: $errorBody")

            val errorMsg = try {
                JSONObject(errorBody ?: "").optString("msg", "Error al obtener datos del sensor")
            } catch (jsonException: JSONException) {
                Log.e("SensorError", "Error al parsear JSON: ${jsonException.message}")
                "Error al obtener datos del sensor"
            }
            Result.failure(Exception(errorMsg))
        } catch (e: IOException) {
            Log.e("SensorError", "Error de red: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: Exception) {
            Log.e("SensorError", e.message.orEmpty())
            Result.failure(Exception("Error en la petición: ${e.message}"))
        }
    }

}