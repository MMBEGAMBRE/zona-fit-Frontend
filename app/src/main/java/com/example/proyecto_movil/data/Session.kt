package com.example.proyecto_movil.data

object Session {
    var token: String? = null
    var userName: String? = null
    var isAdmin: Boolean = false

    // Caché simple para evitar recargas constantes (Optimización Exposición)
    var cacheClientes: List<ClienteResponse>? = null
    var cacheMembresias: List<MembresiaResponse>? = null

    fun bearer(): String = "Bearer $token"

    fun clear() {
        token = null
        userName = null
        isAdmin = false
        cacheClientes = null
        cacheMembresias = null
    }
}
