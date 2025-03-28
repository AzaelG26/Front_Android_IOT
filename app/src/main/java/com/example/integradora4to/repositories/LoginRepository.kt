package com.example.integradora4to.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.integradora4to.models.request.LoginRequest
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    suspend fun login(email: String, password: String): Result<LoginResponse>{
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.logIn(LoginRequest(email, password))
            }
            if (response.tkn != null) {
                saveToken(response.tkn, response.usr.username)
                Result.success(response)
            } else {
                Result.failure(Exception(response.msg))
            }
        }catch (e: Exception) {
            val errorBody = (e as? retrofit2.HttpException)?.response()?.errorBody()?.string()
            val errorMsg = errorBody?.let {
                JSONObject(it).optString("msg", "Error en la autenticación")
            } ?: "Error en la autenticación: ${e.message}"

            Result.failure(Exception(errorMsg))

        }
    }

    private fun saveToken(token: String, username: String){
        sharedPreferences.edit().putString("auth_token", token).putString("username", username).apply()
    }

    fun getToken(): String?{
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUsername(): String?{
        return sharedPreferences.getString("username", null)
    }

    fun logOut() {
        sharedPreferences.edit().remove("auth_token").remove("username").apply()
    }
}