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

// -------- Lo que ENVIAMOS para crear una membresía --------
data class CreateMembresiaRequest(
    val cliente_id: Int,
    val tipo: String,
    val fecha_inicio: String,
    val fecha_vencimiento: String
)

// -------- Registrar cliente + membresía + pago en un solo paso --------
// fecha_vencimiento NO se envía: la calcula el backend según 'tipo' y 'fecha_inicio'.
// El pago es obligatorio: metodo_pago y monto siempre deben ir informados.
data class CreateClienteConMembresiaRequest(
    val nombre: String,
    val apellido: String,
    val documento: String,
    val email: String,
    val telefono: String,
    val fecha_nacimiento: String,
    val tipo: String,
    val fecha_inicio: String,
    val metodo_pago: String,
    val monto: Double
)

// -------- Registrar un pago para un cliente/membresía ya existentes --------
data class CreatePagoRequest(
    val cliente_id: Int,
    val membresia_id: Int,
    val monto: Double,
    val metodo_pago: String
)

data class ClienteConMembresiaResponse(
    val message: String?,
    val cliente: ClienteResponse?,
    val membresia: MembresiaResponse?,
    val pago: PagoResponse?
)

data class MembresiaResponse(
    val id: Int,
    val cliente_id: Int,
    val tipo: String,
    val fecha_inicio: String,
    val fecha_vencimiento: String,
    val estado: String,
    val cliente_nombre: String?,
    val cliente_apellido: String?
)

data class PagoResponse(
    val id: Int,
    val cliente_id: Int,
    val membresia_id: Int,
    val monto: Double,
    val metodo_pago: String,
    val fecha_pago: String,
    val nombre: String?,
    val apellido: String?,
    val membresia_tipo: String?
)

data class UpdateClientRequest(
    val nombre: String,
    val apellido: String,
    val documento: String,
    val email: String,
    val telefono: String,
    val fecha_nacimiento: String,
    val estado: String
)


// -------- Perfil propio --------
data class ProfileUser(val id: Int, val nombre: String, val email: String, val rol: String)
data class ProfileResponse(val message: String?, val user: ProfileUser?)
data class UpdateProfileRequest(val nombre: String, val email: String)
data class ChangePasswordRequest(val current_password: String, val new_password: String)
data class SimpleMessageResponse(val message: String?)
