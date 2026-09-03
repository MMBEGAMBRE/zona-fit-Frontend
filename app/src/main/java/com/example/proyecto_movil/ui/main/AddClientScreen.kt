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
import com.example.proyecto_movil.data.CreateClientRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNac by remember { mutableStateOf("1990-01-01") } // Formato YYYY-MM-DD

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Socio", fontWeight = FontWeight.Bold, color = Color.Black) },
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Información",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            CustomOutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
            Spacer(modifier = Modifier.height(12.dp))

            // Campo Apellido
            CustomOutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido")
            Spacer(modifier = Modifier.height(12.dp))

            // Campo Documento
            CustomOutlinedTextField(value = documento, onValueChange = { documento = it }, label = "Número de Cédula")
            Spacer(modifier = Modifier.height(12.dp))

            // Campo Email
            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico")
            Spacer(modifier = Modifier.height(12.dp))

            // Campo Teléfono
            CustomOutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = "Teléfono")
            Spacer(modifier = Modifier.height(12.dp))

            // Campo Fecha (Simplificado)
            CustomOutlinedTextField(value = fechaNac, onValueChange = { fechaNac = it }, label = "Fecha Nac. (YYYY-MM-DD)")

            Spacer(modifier = Modifier.height(32.dp))

            if (message.isNotEmpty()) {
                Text(text = message, color = if (message.contains("éxito")) Color.Green else Color.Red, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || apellido.isBlank() || documento.isBlank()) {
                        message = "⚠️ Nombre, Apellido y Cédula son obligatorios"
                        return@Button
                    }
                    
                    isLoading = true
                    message = ""
                    
                    scope.launch {
                        try {
                            val request = CreateClientRequest(nombre, apellido, documento, email, telefono, fechaNac)
                            val response = RetrofitClient.api.createCliente(Session.bearer(), request)
                            
                            if (response.isSuccessful) {
                                message = "✅ Socio registrado con éxito"
                                // Volvemos después de un segundo para que vea el mensaje
                                kotlinx.coroutines.delay(1000)
                                navController.popBackStack()
                            } else {
                                message = "❌ Error: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            message = "⚠️ Error de red: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("GUARDAR SOCIO", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZonaFitYellow,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = ZonaFitYellow,
            unfocusedLabelColor = Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}
