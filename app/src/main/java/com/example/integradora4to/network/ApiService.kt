package com.example.integradora4to.network

import com.example.integradora4to.models.LoginRequest
import com.example.integradora4to.models.LoginResponse
import com.example.integradora4to.models.RegisterRequest
import com.example.integradora4to.models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun logIn(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}