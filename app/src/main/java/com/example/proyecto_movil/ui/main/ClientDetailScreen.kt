package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.ClienteResponse
import com.example.proyecto_movil.data.MembresiaResponse
import com.example.proyecto_movil.data.PagoResponse
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
fun ClientDetailScreen(navController: NavController, clienteId: Int) {
    var cliente by remember { mutableStateOf<ClienteResponse?>(null) }
    var membresias by remember { mutableStateOf<List<MembresiaResponse>>(emptyList()) }
    var pagos by remember { mutableStateOf<List<PagoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(clienteId) {
        scope.launch {
            try {
                // Obtenemos datos del cliente
                val resC = RetrofitClient.api.getCliente(Session.bearer(), clienteId)
                if (resC.isSuccessful) cliente = resC.body()

                // Obtenemos TODAS las membresías y pagos y filtramos por cliente
                // (Idealmente el backend debería tener un endpoint filtrado)
                val resM = RetrofitClient.api.getMembresias(Session.bearer())
                if (resM.isSuccessful) {
                    membresias = resM.body()?.filter { it.cliente_id == clienteId } ?: emptyList()
                }

                val resP = RetrofitClient.api.getPagos(Session.bearer())
                if (resP.isSuccessful) {
                    pagos = resP.body()?.filter { it.cliente_id == clienteId } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Socio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit_cliente/$clienteId") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    if (Session.isAdmin) {
                        IconButton(onClick = {
                            scope.launch {
                                try {
                                    val resp = RetrofitClient.api.deleteCliente(Session.bearer(), clienteId)
                                    if (resp.isSuccessful) navController.popBackStack()
                                } catch (_: Exception) {}
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZonaFitYellow)
            )
        },
        containerColor = ZonaFitDark
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ZonaFitYellow)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    cliente?.let {
                        Text(text = "Información Personal", color = ZonaFitYellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("${it.nombre} ${it.apellido}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Cédula: ${it.documento}", color = Color.LightGray)
                                Text("Email: ${it.email ?: "N/A"}", color = Color.LightGray)
                                Text("Tel: ${it.telefono ?: "N/A"}", color = Color.LightGray)
                            }
                        }
                    }
                }

                item {
                    Text(text = "Historial de Membresías", color = ZonaFitYellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                if (membresias.isEmpty()) {
                    item { Text("No hay membresías registradas", color = Color.Gray) }
                } else {
                    items(membresias) { m ->
                        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val esVencida = m.fecha_vencimiento < hoy

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Plan ${m.tipo}", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("Vence: ${m.fecha_vencimiento}", color = Color.LightGray, fontSize = 12.sp)
                                }
                                Surface(
                                    color = if (esVencida) Color.Red else Color.Green,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (esVencida) "VENCIDA" else "ACTIVA",
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(text = "Historial de Pagos", color = ZonaFitYellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                if (pagos.isEmpty()) {
                    item { Text("No hay pagos registrados", color = Color.Gray) }
                } else {
                    items(pagos) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Monto: $${p.monto}", color = Color.White)
                                    Text(p.metodo_pago, color = Color.LightGray, fontSize = 12.sp)
                                }
                                Text(p.fecha_pago, color = ZonaFitYellow, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
