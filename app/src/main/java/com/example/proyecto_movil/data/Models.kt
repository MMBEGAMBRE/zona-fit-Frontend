package com.example.proyecto_movil.data

// Models.kt — clases de datos que representan lo que ENVIAMOS y RECIBIMOS del backend.

// -------- Lo que ENVIAMOS al hacer login --------
data class LoginRequest(
    val Email: String,
    val PasswoRDkey: String
)

// -------- Lo que RECIBIMOS si el login es correcto --------
data class LoginResponse(
    val message: String?,
    val token: String?,
    val user: User?
)

// -------- Datos del usuario que vienen dentro de la respuesta --------
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String
)

// -------- Datos de Clientes --------
data class ClienteResponse(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val email: String?,
    val telefono: String?,
    val fecha_nacimiento: String?,
    val estado: String?
)

// -------- Datos de Registros/Auditoría (Solo Admin) --------
data class RegistroResponse(
    val id: Int,
    val fecha_hora: String,
    val accion: String,
    val descripcion: String,
    val ip: String?,
    val usuario_nombre: String?,
    val usuario_rol: String?
)

// -------- Lo que ENVIAMOS para crear un cliente (Guía GA4) --------
data class CreateClientRequest(
    val nombre: String,
    val apellido: String,
    val documento: String,
    val email: String,
    val telefono: String,
    val fecha_nacimiento: String
)

// -------- Registro de Personal (Guía GA3) --------
data class RegisterStaffRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String
)
