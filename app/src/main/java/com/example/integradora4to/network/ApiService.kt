package com.example.integradora4to.network

import com.example.integradora4to.models.request.CreateSafeRequest
import com.example.integradora4to.models.request.LoginRequest
import com.example.integradora4to.models.request.response.LoginResponse
import com.example.integradora4to.models.request.RegisterRequest
import com.example.integradora4to.models.request.UpdateBoxRequest
import com.example.integradora4to.models.request.UpdateUserRequest
import com.example.integradora4to.models.request.UpdateUserResponse
import com.example.integradora4to.models.request.response.CreateSafeResponse
import com.example.integradora4to.models.request.response.RegisterResponse
import com.example.integradora4to.models.request.response.GetBoxResponse
import com.example.integradora4to.models.request.response.SensorDataResponse
import com.example.integradora4to.models.request.response.UpdateBoxResponse
import com.example.integradora4to.models.request.response.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun logIn(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/box/createbox")
    suspend fun createSafe(@Header("Authorization")authorization: String, @Body request: CreateSafeRequest): CreateSafeResponse

    @GET("api/box/find-box/{id}")
    suspend fun showBoxByUserId(@Header("Authorization") authorization: String, @Path("id") id: String): GetBoxResponse

    @PUT("api/box/update-pin")
    suspend fun updatePinOfBox(@Header("Authorization") authorization: String, @Body request: UpdateBoxRequest): UpdateBoxResponse

    @GET("api/user/user/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @GET("api/sensor/dht11")
    suspend fun getSensorData(): SensorDataResponse

    @PUT("api/user/user/profile")
    suspend fun updateUser(@Header("Authorization") authorization: String, @Body request: UpdateUserRequest): UpdateUserResponse
}