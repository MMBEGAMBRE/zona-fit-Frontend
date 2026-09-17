package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.MembresiaResponse
import com.example.proyecto_movil.data.RenovacionRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembresiasScreen(navController: NavController) {
    var membresias by remember { mutableStateOf<List<MembresiaResponse>>(Session.cacheMembresias ?: emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(Session.cacheMembresias == null) }
    var errorMessage by remember { mutableStateOf("") }

    // Estado para el diálogo de renovación
    var mostrarDialogoRenovacion by remember { mutableStateOf(false) }
    var membresiaARenovar by remember { mutableStateOf<MembresiaResponse?>(null) }
    var montoRenovacion by remember { mutableStateOf("") }
    var metodoPagoRenovacion by remember { mutableStateOf(METODOS_PAGO.first()) }
    var renovando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun cargarMembresias() {
        if (membresias.isEmpty()) isLoading = true
        scope.launch {
            try {
                val resp = RetrofitClient.api.getMembresias(Session.bearer())
                if (resp.isSuccessful) {
                    val data = resp.body() ?: emptyList()
                    membresias = data
                    Session.cacheMembresias = data // Actualizar caché
                } else errorMessage = "Error al obtener membresías"
            } catch (e: Exception) {
                if (membresias.isEmpty()) errorMessage = "⚠️ Error de conexión"
            } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) {
        cargarMembresias()
    }

    // Optimización: Filtrado eficiente usando remember
    val membresiasFiltradas = remember(searchQuery, membresias) {
        if (searchQuery.isEmpty()) membresias
        else membresias.filter { m ->
            m.cliente_nombre?.contains(searchQuery, ignoreCase = true) == true ||
            m.cliente_apellido?.contains(searchQuery, ignoreCase = true) == true ||
            m.cliente_documento?.contains(searchQuery) == true
        }
    }

    if (mostrarDialogoRenovacion && membresiaARenovar != null) {
        AlertDialog(
            onDismissRequest = { if (!renovando) mostrarDialogoRenovacion = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("Renovar Membresía", color = ZonaFitYellow) },
            text = {
                Column {
                    Text("Socio: ${membresiaARenovar!!.cliente_nombre} ${membresiaARenovar!!.cliente_apellido}", color = Color.White)
                    Text("Plan actual: ${membresiaARenovar!!.tipo}", color = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = montoRenovacion,
                        onValueChange = { montoRenovacion = it },
                        label = { Text("Monto a cobrar") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(Modifier.height(8.dp))
                    MetodoPagoDropdown(metodo = metodoPagoRenovacion, onMetodoChange = { metodoPagoRenovacion = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val monto = montoRenovacion.toDoubleOrNull() ?: 0.0
                        if (monto <= 0) return@Button

                        renovando = true
                        scope.launch {
                            try {
                                val req = RenovacionRequest(membresiaARenovar!!.id, monto, metodoPagoRenovacion)
                                val resp = RetrofitClient.api.renovarMembresia(Session.bearer(), req)
                                if (resp.isSuccessful) {
                                    cargarMembresias()
                                    mostrarDialogoRenovacion = false
                                }
                            } catch (_: Exception) {
                            } finally {
                                renovando = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow),
                    enabled = !renovando
                ) {
                    if (renovando) CircularProgressIndicator(Modifier.size(20.dp), color = Color.Black)
                    else Text("PAGAR Y ACTIVAR", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRenovacion = false }, enabled = !renovando) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Renovación de Membresías", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_membresia") }) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Membresía", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZonaFitYellow)
            )
        },
        containerColor = ZonaFitDark
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Buscador por nombre/cédula
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Renovar: Buscar socio por nombre o CC...") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZonaFitYellow,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ZonaFitYellow)
                    errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                    membresiasFiltradas.isEmpty() -> Text("No se encontraron resultados", color = Color.White, modifier = Modifier.align(Alignment.Center))
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(membresiasFiltradas) { m ->
                            val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val esVencida = m.fecha_vencimiento < hoy

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${m.cliente_nombre ?: ""} ${m.cliente_apellido ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("CC: ${m.cliente_documento ?: "N/A"}", color = Color.LightGray, fontSize = 11.sp)
                                        Text("Plan: ${m.tipo}", color = Color.LightGray, fontSize = 13.sp)
                                        Text("Vence: ${m.fecha_vencimiento}", color = Color.LightGray, fontSize = 13.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            color = if (esVencida) Color.Red else Color.Green,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (esVencida) "VENCIDA" else m.estado,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        if (esVencida) {
                                            Spacer(Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    membresiaARenovar = m
                                                    montoRenovacion = PRECIOS_SUGERIDOS[m.tipo]?.toInt()?.toString() ?: ""
                                                    mostrarDialogoRenovacion = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text("RENOVAR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
