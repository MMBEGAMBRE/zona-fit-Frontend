package com.example.proyecto_movil.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Opción A: para emulador de Android Studio (IP especial que apunta a tu PC)
    // private const val BASE_URL = "http://10.0.2.2:5050/"

    // Opción B: para celular físico en la misma red Wi-Fi que tu PC
    // private const val BASE_URL = "http://192.168.1.65:5050/"

    // Opción C: Despliegue en la nube (Render)
    private const val BASE_URL = "https://zona-fit-backend-ds3y.onrender.com/"


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo máximo para conectar
        .readTimeout(60, TimeUnit.SECONDS)    // Tiempo máximo para recibir datos
        .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo máximo para enviar datos
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
