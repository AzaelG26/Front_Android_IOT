package com.example.integradora4to.models.request.response

data class LoginResponse(
    val tkn: String? = null,
    val usr: User,
    val msg: String
)

