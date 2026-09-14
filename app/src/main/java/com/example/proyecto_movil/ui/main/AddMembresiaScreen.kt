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
import com.example.proyecto_movil.data.ClienteResponse
import com.example.proyecto_movil.data.CreateMembresiaConPagoRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.esFechaValida
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val TIPOS_MEMBRESIA = listOf("Mensual", "Trimestral", "Anual")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMembresiaScreen(navController: NavController) {
    var clientes by remember { mutableStateOf<List<ClienteResponse>>(emptyList()) }
    var clienteSeleccionado by remember { mutableStateOf<ClienteResponse?>(null) }
    var tipo by remember { mutableStateOf(TIPOS_MEMBRESIA.first()) }
    var fechaInicio by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var fechaVencimiento by remember { mutableStateOf("") }

    // Campos de Pago Integrado
    var monto by remember { mutableStateOf(PRECIOS_SUGERIDOS[TIPOS_MEMBRESIA.first()]?.toInt()?.toString() ?: "") }
    var metodoPago by remember { mutableStateOf(METODOS_PAGO.first()) }

    var cargandoClientes by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Función para calcular fecha fin automáticamente
    fun calcularFechaVencimiento(inicio: String, plan: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(inicio) ?: return ""
            val cal = Calendar.getInstance()
            cal.time = date

            val meses = when(plan) {
                "Mensual" -> 1
                "Trimestral" -> 3
                "Anual" -> 12
                else -> 1
            }
            cal.add(Calendar.MONTH, meses)
            sdf.format(cal.time)
        } catch (e: Exception) { "" }
    }

    // Recalcular cuando cambie el tipo o la fecha de inicio
    LaunchedEffect(tipo, fechaInicio) {
        fechaVencimiento = calcularFechaVencimiento(fechaInicio, tipo)
        monto = PRECIOS_SUGERIDOS[tipo]?.toInt()?.toString() ?: ""
    }

    // Carga la lista de clientes para poder elegir a quién se le asigna la membresía
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getClientes(Session.bearer())
                if (response.isSuccessful) {
                    clientes = response.body() ?: emptyList()
                } else {
                    message = manejarError(response.code(), navController)
                }
            } catch (e: Exception) {
                message = "📡 Sin conexión al servidor"
            } finally {
                cargandoClientes = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Membresía", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                text = "Asignar Plan",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (cargandoClientes) {
                CircularProgressIndicator(color = ZonaFitYellow)
            } else if (clientes.isEmpty()) {
                Text(
                    text = "No hay clientes registrados todavía. Crea un cliente primero.",
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            } else {
                // Selector de cliente
                ClienteDropdown(
                    clientes = clientes,
                    seleccionado = clienteSeleccionado,
                    onSeleccionar = { clienteSeleccionado = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Selector de tipo de plan
                TipoDropdown(tipo = tipo, onTipoChange = { tipo = it })
                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = "Fecha inicio (YYYY-MM-DD)"
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    value = fechaVencimiento,
                    onValueChange = { fechaVencimiento = it },
                    label = "Fecha vencimiento (Calculada)"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Información del Pago",
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
                    Text(
                        text = message,
                        color = if (message.contains("éxito")) Color.Green else Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val cliente = clienteSeleccionado
                        when {
                            cliente == null -> {
                                message = "⚠️ Selecciona un cliente"; return@Button
                            }
                            !esFechaValida(fechaInicio) -> {
                                message = "⚠️ La fecha de inicio debe tener formato YYYY-MM-DD"; return@Button
                            }
                            monto.toDoubleOrNull() == null || monto.toDouble() <= 0.0 -> {
                                message = "⚠️ El monto es obligatorio"; return@Button
                            }
                        }

                        isLoading = true
                        message = ""

                        scope.launch {
                            try {
                                val request = CreateMembresiaConPagoRequest(
                                    cliente_id = cliente!!.id,
                                    tipo = tipo,
                                    fecha_inicio = fechaInicio,
                                    fecha_vencimiento = fechaVencimiento,
                                    monto = monto.toDouble(),
                                    metodo_pago = metodoPago
                                )
                                val response = RetrofitClient.api.createMembresiaConPago(Session.bearer(), request)

                                if (response.isSuccessful) {
                                    message = "✅ Membresía y pago registrados"
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
                        Text("GUARDAR Y ACTIVAR", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClienteDropdown(
    clientes: List<ClienteResponse>,
    seleccionado: ClienteResponse?,
    onSeleccionar: (ClienteResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val etiqueta = seleccionado?.let { "${it.nombre} ${it.apellido} (CC: ${it.documento})" } ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = etiqueta,
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente") },
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
            clientes.forEach { cliente ->
                DropdownMenuItem(
                    text = { Text("${cliente.nombre} ${cliente.apellido} (CC: ${cliente.documento})") },
                    onClick = {
                        onSeleccionar(cliente)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoDropdown(tipo: String, onTipoChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = tipo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de plan") },
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
            TIPOS_MEMBRESIA.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onTipoChange(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
