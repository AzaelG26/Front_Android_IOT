package com.example.integradora4to.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.integradora4to.models.request.UpdateBoxRequest
import com.example.integradora4to.models.request.response.GetBoxResponse
import com.example.integradora4to.models.request.response.UpdateBoxResponse
import com.example.integradora4to.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SafeRepository(private val apiService: ApiService, context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    suspend fun getBoxByUserId(): Result<GetBoxResponse> {
        return try {
            val token = sharedPreferences.getString("auth_token", null)
            val userId = sharedPreferences.getString("user_id", null)
            if (token.isNullOrEmpty()){
                return Result.failure(Exception("Token not sent"))
            }
            if (userId.isNullOrEmpty()){
                return Result.failure(Exception("User id not sent"))
            }
            val response = withContext(Dispatchers.IO){
                apiService.showBoxByUserId("Bearer $token", userId)
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePinOfSafe(vaultId: String, newPin: String): Result<UpdateBoxResponse>{
        return try {
            val token = sharedPreferences.getString("auth_token", null)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token not sent"))
            }
            val response = withContext(Dispatchers.IO){
                apiService.updatePinOfBox("Bearer $token", UpdateBoxRequest(vaultId, newPin))
            }
            Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

}