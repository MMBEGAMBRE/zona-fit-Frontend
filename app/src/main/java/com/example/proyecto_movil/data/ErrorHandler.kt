package com.example.proyecto_movil.data

import androidx.navigation.NavController

// Traduce un código HTTP a un mensaje entendible.
// Si es 401, además limpia la sesión y devuelve al login.
fun manejarError(code: Int, navController: NavController? = null): String {
    return when (code) {
        401 -> {
            Session.clear()
            navController?.navigate("login") { popUpTo(0) }
            "⚠️ Tu sesión expiró, vuelve a iniciar sesión"
        }
        403 -> " No tienes permisos para esta acción"
        404 -> " No se encontró el recurso solicitado"
        409 -> " El dato ya existe (conflicto)"
        else -> " Error inesperado (código $code)"
    }
}
