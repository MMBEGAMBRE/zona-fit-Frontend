package com.example.proyecto_movil.data

object Session {
    var token: String? = null
    var userName: String? = null
    var isAdmin: Boolean = false

    fun bearer(): String = "Bearer $token"

    fun clear() {
        token = null
        userName = null
        isAdmin = false
    }
}
