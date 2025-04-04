package com.example.integradora4to.repositories

import com.example.integradora4to.models.request.UpdateUserRequest
import com.example.integradora4to.models.request.UpdateUserResponse
import com.example.integradora4to.models.request.response.User
import com.example.integradora4to.network.ApiService
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(id: String): Result<User>{
        return try {
            val response: User = apiService.getUser(id)
            Result.success(response)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateUser(token: String, request: UpdateUserRequest): Result<UpdateUserResponse> {
        return try {
            val response = apiService.updateUser("Bearer $token", request)
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                JSONObject(it).optString("msg", "Error desconocido al actualizar usuario")
            } ?: "Error HTTP ${e.code()}"

            if (e.code() == 401 && errorMessage.contains("Token expirado", ignoreCase = true)) {
                return Result.failure(Exception("Token expirado"))
            }

            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}