import android.util.Log
import com.example.integradora4to.ErrorResponse
import com.example.integradora4to.models.request.RegisterRequest
import com.example.integradora4to.network.RetrofitClient
import com.example.integradora4to.models.request.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import retrofit2.HttpException

class RegisterRepository {
    suspend fun register(username: String, phone: String, email: String, password: String): Result<RegisterResponse> {
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
            Log.d("RegisterRepository", "Response: $response")
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("RegisterRepository", "HttpException raw body: $errorBody")

            if (!errorBody.isNullOrEmpty()) {
                try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Log.e("RegisterRepository", "HttpException: ${errorResponse.msg}")
                    Result.failure(Exception(errorResponse.msg))
                } catch (jsonException: Exception) {
                    Log.e("RegisterRepository", "JSONException while parsing error body: ${jsonException.message}")
                    Result.failure(Exception("Error en el registro"))
                }
            } else {
                Log.e("RegisterRepository", "HttpException: respuesta vacía del servidor")
                Result.failure(Exception("Error en el registro: respuesta vacía del servidor"))
            }
        } catch (e: Exception) {
            Log.e("RegisterRepository", "Exception: ${e.message}")
            Result.failure(Exception("Error en el registro: ${e.message}"))
        }
    }
}