package com.example.integradora4to.models.request.response

data class CreateSafeResponse(
    val msg: String,
    val box: List<Box>
)

data class Box(
    val id: String,
    val nickname: String,
    val status: Boolean,
    val userId: String
)

