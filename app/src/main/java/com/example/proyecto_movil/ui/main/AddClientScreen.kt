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
import com.example.proyecto_movil.data.CreateClienteConMembresiaRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.esEmailValido
import com.example.proyecto_movil.data.esFechaValida
import com.example.proyecto_movil.data.esTelefonoValido
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun hoyComoTexto(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

val METODOS_PAGO = listOf("Efectivo", "Transferencia", "Tarjeta")

// Precios sugeridos solo para precargar el campo "Monto" — el empleado puede
// editarlo (descuentos, promociones, etc.). El backend no depende de estos valores.
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

    // Membresía que se registra en el mismo momento que el cliente
    var tipoPlan by remember { mutableStateOf(TIPOS_MEMBRESIA.first()) }
    var fechaInicioPlan by remember { mutableStateOf(hoyComoTexto()) }

    // Pago: obligatorio, se registra en el mismo paso
    var metodoPago by remember { mutableStateOf(METODOS_PAGO.first()) }
    var monto by remember { mutableStateOf(PRECIOS_SUGERIDOS.getValue(TIPOS_MEMBRESIA.first()).toInt().toString()) }

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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Membresía inicial",
                color = ZonaFitYellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Plan que se le asigna al cliente desde ya (el vencimiento lo calcula el backend)
            TipoDropdown(
                tipo = tipoPlan,
                onTipoChange = {
                    tipoPlan = it
                    monto = PRECIOS_SUGERIDOS.getValue(it).toInt().toString()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = fechaInicioPlan,
                onValueChange = { fechaInicioPlan = it },
                label = "Fecha inicio del plan (YYYY-MM-DD)"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pago (obligatorio)",
                color = ZonaFitYellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            MetodoPagoDropdown(metodo = metodoPago, onMetodoChange = { metodoPago = it })
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = "Monto pagado"
            )

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
                        !esFechaValida(fechaInicioPlan) -> {
                            message = "⚠️ La fecha de inicio del plan debe tener formato YYYY-MM-DD"; return@Button
                        }
                        monto.toDoubleOrNull() == null || monto.toDouble() <= 0.0 -> {
                            message = "⚠️ El monto pagado es obligatorio y debe ser mayor a 0"; return@Button
                        }
                    }

                    isLoading = true
                    message = ""

                    scope.launch {
                        try {
                            val request = CreateClienteConMembresiaRequest(
                                nombre = nombre,
                                apellido = apellido,
                                documento = documento,
                                email = email,
                                telefono = telefono,
                                fecha_nacimiento = fechaNac,
                                tipo = tipoPlan,
                                fecha_inicio = fechaInicioPlan,
                                metodo_pago = metodoPago,
                                monto = monto.toDouble()
                            )
                            val response = RetrofitClient.api.createClienteConMembresia(Session.bearer(), request)

                            if (response.isSuccessful) {
                                val vencimiento = response.body()?.membresia?.fecha_vencimiento
                                val pagado = response.body()?.pago?.monto
                                message = "✅ Socio registrado — plan $tipoPlan vence el $vencimiento — pago de $$pagado ($metodoPago) registrado"
                                // Volvemos después de un momento para que vea el mensaje
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
                    Text("GUARDAR SOCIO", color = Color.Black, fontWeight = FontWeight.Bold)
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
