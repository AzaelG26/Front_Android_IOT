package com.example.integradora4to.network

import com.example.integradora4to.models.request.CreateSafeRequest
import com.example.integradora4to.models.request.LoginRequest
import com.example.integradora4to.models.response.LoginResponse
import com.example.integradora4to.models.request.RegisterRequest
import com.example.integradora4to.models.response.CreateSafeResponse
import com.example.integradora4to.models.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun logIn(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/box/createbox")
    suspend fun createSafe(@Header("Authorization")authorization: String, @Body request: CreateSafeRequest): CreateSafeResponse
}