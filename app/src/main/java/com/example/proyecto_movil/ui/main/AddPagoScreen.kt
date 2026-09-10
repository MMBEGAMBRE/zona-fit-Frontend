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
import com.example.proyecto_movil.data.CreatePagoRequest
import com.example.proyecto_movil.data.MembresiaResponse
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPagoScreen(navController: NavController) {
    var membresias by remember { mutableStateOf<List<MembresiaResponse>>(emptyList()) }
    var membresiaSeleccionada by remember { mutableStateOf<MembresiaResponse?>(null) }
    var metodoPago by remember { mutableStateOf(METODOS_PAGO.first()) }
    var monto by remember { mutableStateOf("") }

    var cargandoMembresias by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Carga las membresías existentes para poder elegir a quién se le registra el pago
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getMembresias(Session.bearer())
                if (response.isSuccessful) {
                    membresias = response.body() ?: emptyList()
                } else {
                    message = manejarError(response.code(), navController)
                }
            } catch (e: Exception) {
                message = "📡 Sin conexión al servidor"
            } finally {
                cargandoMembresias = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Pago", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                text = "Cobrar Membresía",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (cargandoMembresias) {
                CircularProgressIndicator(color = ZonaFitYellow)
            } else if (membresias.isEmpty()) {
                Text(
                    text = "No hay membresías registradas todavía. Crea una membresía primero.",
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            } else {
                // Selector de membresía (implica el cliente)
                MembresiaDropdown(
                    membresias = membresias,
                    seleccionada = membresiaSeleccionada,
                    onSeleccionar = {
                        membresiaSeleccionada = it
                        monto = PRECIOS_SUGERIDOS[it.tipo]?.toInt()?.toString() ?: ""
                    }
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
                    Text(
                        text = message,
                        color = if (message.startsWith("✅")) Color.Green else Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val membresia = membresiaSeleccionada
                        when {
                            membresia == null -> {
                                message = "⚠️ Selecciona una membresía"; return@Button
                            }
                            monto.toDoubleOrNull() == null || monto.toDouble() <= 0.0 -> {
                                message = "⚠️ El monto es obligatorio y debe ser mayor a 0"; return@Button
                            }
                        }

                        isLoading = true
                        message = ""

                        scope.launch {
                            try {
                                val request = CreatePagoRequest(
                                    cliente_id = membresia!!.cliente_id,
                                    membresia_id = membresia.id,
                                    monto = monto.toDouble(),
                                    metodo_pago = metodoPago
                                )
                                val response = RetrofitClient.api.createPago(Session.bearer(), request)

                                if (response.isSuccessful) {
                                    message = "✅ Pago registrado con éxito"
                                    kotlinx.coroutines.delay(1000)
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
                        Text("REGISTRAR PAGO", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MembresiaDropdown(
    membresias: List<MembresiaResponse>,
    seleccionada: MembresiaResponse?,
    onSeleccionar: (MembresiaResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val etiqueta = seleccionada?.let {
        "${it.cliente_nombre ?: ""} ${it.cliente_apellido ?: ""} — ${it.tipo}"
    } ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = etiqueta,
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente / membresía") },
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
            membresias.forEach { membresia ->
                DropdownMenuItem(
                    text = {
                        Text("${membresia.cliente_nombre ?: ""} ${membresia.cliente_apellido ?: ""} — ${membresia.tipo} (vence ${membresia.fecha_vencimiento})")
                    },
                    onClick = {
                        onSeleccionar(membresia)
                        expanded = false
                    }
                )
            }
        }
    }
}
