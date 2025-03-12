package com.example.integradora4to.models

data class RegisterRequest (
    val username: String,
    val phone: String,
    val email: String,
    val password: String,
)