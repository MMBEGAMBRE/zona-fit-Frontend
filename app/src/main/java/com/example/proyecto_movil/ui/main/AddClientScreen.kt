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
import com.example.proyecto_movil.data.esEmailValido
import com.example.proyecto_movil.data.esFechaValida
import com.example.proyecto_movil.data.esTelefonoValido
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

val METODOS_PAGO = listOf("Efectivo", "Transferencia", "Tarjeta")

// Precios sugeridos para membresías
val PRECIOS_SUGERIDOS = mapOf(
    "Mensual" to 50000.0,
    "Trimestral" to 135000.0,
    "Anual" to 480000.0
)

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
                title = { Text("Registrar Cliente", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                text = "Datos del Nuevo Socio",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // ... (campos permanecen igual) ...
            Spacer(modifier = Modifier.height(24.dp))

            CustomOutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido")
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(value = documento, onValueChange = { documento = it }, label = "Número de Cédula")
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico")
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = "Teléfono")
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(value = fechaNac, onValueChange = { fechaNac = it }, label = "Fecha Nac. (YYYY-MM-DD)")

            Spacer(modifier = Modifier.height(32.dp))

            if (message.isNotEmpty()) {
                Text(text = message, color = if (message.startsWith("✅")) Color.Green else Color.Red, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || apellido.isBlank() || documento.isBlank() -> {
                            message = "⚠️ Nombre, Apellido y Cédula son obligatorios"; return@Button
                        }
                        email.isNotBlank() && !esEmailValido(email) -> {
                            message = "⚠️ El correo no tiene un formato válido"; return@Button
                        }
                        !esTelefonoValido(telefono) -> {
                            message = "⚠️ El teléfono solo debe contener números"; return@Button
                        }
                        !esFechaValida(fechaNac) -> {
                            message = "⚠️ La fecha debe tener formato YYYY-MM-DD"; return@Button
                        }
                    }

                    isLoading = true
                    message = ""

                    scope.launch {
                        try {
                            val request = CreateClientRequest(
                                nombre = nombre,
                                apellido = apellido,
                                documento = documento,
                                email = email,
                                telefono = telefono,
                                fecha_nacimiento = fechaNac
                            )
                            // Usamos el endpoint que solo crea el cliente
                            val response = RetrofitClient.api.createCliente(Session.bearer(), request)

                            if (response.isSuccessful) {
                                message = "✅ Cliente registrado con éxito. Ahora puedes asignarle una membresía."
                                kotlinx.coroutines.delay(1500)
                                navController.popBackStack()
                            } else {
                                message = manejarError(response.code(), navController)
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
                    Text("REGISTRAR CLIENTE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetodoPagoDropdown(metodo: String, onMetodoChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = metodo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Método de pago") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZonaFitYellow,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = ZonaFitYellow,
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            METODOS_PAGO.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onMetodoChange(opcion)
                        expanded = false
                    }
                )
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
