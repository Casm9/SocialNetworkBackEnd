package com.casm.data.requests

data class CreateAccountRequest(
    val email: String,
    val username: String,
    val password: String
)