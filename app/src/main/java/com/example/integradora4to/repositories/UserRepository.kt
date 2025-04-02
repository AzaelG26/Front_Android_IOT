package com.example.integradora4to.repositories

import com.example.integradora4to.models.request.response.User
import com.example.integradora4to.network.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(id: String): Result<User>{
        return try {
            val response: User = apiService.getUser(id);
            Result.success(response)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }
}