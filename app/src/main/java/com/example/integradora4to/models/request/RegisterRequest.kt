package com.example.integradora4to.models.request

data class RegisterRequest (
    val username: String,
    val phone: String,
    val email: String,
    val password: String,
)
