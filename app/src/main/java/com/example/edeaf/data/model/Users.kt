package com.example.edeaf.data.model

data class Users (
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null,
)

fun Users.initial(): Users{
    return Users(
        userId = "",
        name = "",
        email = "",
        password = "",
        role = ""
    )
}