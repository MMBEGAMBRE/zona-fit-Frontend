package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.UpdateClientRequest
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientScreen(navController: NavController, clienteId: Int) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNac by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("ACTIVO") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Precarga los datos actuales del cliente (necesitas GET /api/clientes/<id> en el ApiService)
    LaunchedEffect(clienteId) {
        try {
            val resp = RetrofitClient.api.getCliente(Session.bearer(), clienteId)
            if (resp.isSuccessful) {
                resp.body()?.let {
                    nombre = it.nombre; apellido = it.apellido; documento = it.documento
                    email = it.email ?: ""; telefono = it.telefono ?: ""
                    fechaNac = it.fecha_nacimiento ?: ""; estado = it.estado ?: "ACTIVO"
                }
            }
        } catch (_: Exception) { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Socio", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZonaFitYellow)
            )
        },
        containerColor = ZonaFitDark
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomOutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = documento, onValueChange = { documento = it }, label = "Cédula")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Correo")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = "Teléfono")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = fechaNac, onValueChange = { fechaNac = it }, label = "Fecha Nac. (YYYY-MM-DD)")
            Spacer(Modifier.height(24.dp))

            if (message.isNotEmpty()) {
                Text(message, color = if (message.contains("éxito")) Color.Green else Color.Red, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    loading = true; message = ""
                    scope.launch {
                        try {
                            val req = UpdateClientRequest(nombre, apellido, documento, email, telefono, fechaNac, estado)
                            val resp = RetrofitClient.api.updateCliente(Session.bearer(), clienteId, req)
                            if (resp.isSuccessful) {
                                message = "✅ Socio actualizado con éxito"
                                kotlinx.coroutines.delay(800)
                                navController.popBackStack()
                            } else {
                                message = "❌ Error: ${resp.code()}"
                            }
                        } catch (e: Exception) {
                            message = "⚠️ ${e.message}"
                        } finally { loading = false }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                else Text("GUARDAR CAMBIOS", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

