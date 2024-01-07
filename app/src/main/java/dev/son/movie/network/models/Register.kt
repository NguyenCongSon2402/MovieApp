package dev.son.movie.network.models

data class Register(
    val name: String,
    val password: String,
    val email: String,
    val birthday: String
)
