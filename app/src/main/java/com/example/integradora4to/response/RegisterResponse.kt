package com.example.integradora4to.response

data class RegisterResponse (
    val msg: String,
    val usr: User
)

data class User(
    val id: String,
    val username: String,
    val phone: String,
    val email: String,
    val status: Boolean
)
