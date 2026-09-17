package com.example.proyecto_movil.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("api/cuentas/profile")
    suspend fun getProfile(@Header("Authorization") auth: String): Response<ProfileResponse>

    @PUT("api/cuentas/profile")
    suspend fun updateProfile(
        @Header("Authorization") auth: String,
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>

    @PUT("api/cuentas/change-password")
    suspend fun changePassword(
        @Header("Authorization") auth: String,
        @Body request: ChangePasswordRequest
    ): Response<SimpleMessageResponse>

    @GET("api/membresias/")
    suspend fun getMembresias(@Header("Authorization") auth: String): Response<List<MembresiaResponse>>

    // Crear una nueva membresía para un cliente
    @POST("api/membresias/")
    suspend fun createMembresia(
        @Header("Authorization") auth: String,
        @Body request: CreateMembresiaRequest
    ): Response<SimpleMessageResponse>

    @POST("api/membresias/")
    suspend fun createMembresiaConPago(
        @Header("Authorization") auth: String,
        @Body request: CreateMembresiaConPagoRequest
    ): Response<SimpleMessageResponse>

    @GET("api/pagos/")
    suspend fun getPagos(@Header("Authorization") auth: String): Response<List<PagoResponse>>

    // Registrar un pago para un cliente y membresía que ya existen
    @POST("api/pagos/")
    suspend fun createPago(
        @Header("Authorization") auth: String,
        @Body request: CreatePagoRequest
    ): Response<SimpleMessageResponse>

    @GET("api/clientes/{id}")
    suspend fun getCliente(@Header("Authorization") auth: String, @Path("id") id: Int): Response<ClienteResponse>

    @GET("api/clientes/buscar/{documento}")
    suspend fun buscarCliente(@Header("Authorization") auth: String, @Path("documento") documento: String): Response<ClienteResponse>

    @PUT("api/clientes/{id}")
    suspend fun updateCliente(
        @Header("Authorization") auth: String,
        @Path("id") id: Int,
        @Body request: UpdateClientRequest
    ): Response<SimpleMessageResponse>

    @DELETE("api/clientes/{id}")
    suspend fun deleteCliente(@Header("Authorization") auth: String, @Path("id") id: Int): Response<SimpleMessageResponse>

    @GET("api/auth/users")
    suspend fun getUsers(@Header("Authorization") auth: String): Response<List<User>>

    @DELETE("api/auth/users/{id}")
    suspend fun deleteUser(@Header("Authorization") auth: String, @Path("id") id: Int): Response<SimpleMessageResponse>

    @POST("api/membresias/renovar")
    suspend fun renovarMembresia(@Header("Authorization") auth: String, @Body request: RenovacionRequest): Response<SimpleMessageResponse>
}
