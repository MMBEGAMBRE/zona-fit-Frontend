package com.example.proyecto_movil.data

fun esEmailValido(email: String): Boolean =
    email.isNotBlank() && email.contains("@") && email.substringAfter("@").contains(".")

fun esTelefonoValido(telefono: String): Boolean =
    telefono.isBlank() || telefono.all { it.isDigit() }   // vacío se permite, si no, solo dígitos

fun esFechaValida(fecha: String): Boolean =
    Regex("""\d{4}-\d{2}-\d{2}""").matches(fecha)
