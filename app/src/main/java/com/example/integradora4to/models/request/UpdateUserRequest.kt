package com.example.integradora4to.models.request

data class UpdateUserRequest(
    val username: String?,
    val phone: String?,
    val email: String?,
    val password: String?,
    val passwordConfirmation: String?
)
