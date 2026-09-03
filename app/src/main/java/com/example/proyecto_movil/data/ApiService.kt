package com.example.proyecto_movil.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/clientes/")
    suspend fun getClientes(@Header("Authorization") auth: String): Response<List<ClienteResponse>>

    // Crear un nuevo cliente (Guía GA4 - Práctica Final)
    @POST("api/clientes/")
    suspend fun createCliente(
        @Header("Authorization") auth: String,
        @Body request: CreateClientRequest
    ): Response<LoginResponse>

    // Obtener Registros de Auditoría (Solo para el Dueño/Admin)
    @GET("api/registros/")
    suspend fun getRegistros(@Header("Authorization") auth: String): Response<List<RegistroResponse>>

    // Registrar nuevo personal (Dueño registra a empleados)
    @POST("api/auth/register")
    suspend fun registerStaff(
        @Header("Authorization") auth: String,
        @Body request: RegisterStaffRequest
    ): Response<LoginResponse>
}
