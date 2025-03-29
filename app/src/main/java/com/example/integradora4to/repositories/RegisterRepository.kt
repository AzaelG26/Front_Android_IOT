package com.example.integradora4to.repositories
import android.util.Log
import com.example.integradora4to.models.request.RegisterRequest
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.models.request.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RegisterRepository {
    suspend fun register(username: String, phone: String, email:String, password: String ): Result<RegisterResponse> {
            return try {
                val response: RegisterResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.register(
                        RegisterRequest(
                            username,
                            phone,
                            email,
                            password
                        )
                    )
                }
                Result.success(response)

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = errorBody?.let {
                    JSONObject(it).optString("msg", "Error en el registro")
                } ?: "Error en el registro"

                Result.failure(Exception(errorMsg))
            } catch (e: Exception) {
                Log.e("RegisterError", e.message.orEmpty())
                Result.failure(Exception("Error en el registro: ${e.message}"))
            }

    }
}
