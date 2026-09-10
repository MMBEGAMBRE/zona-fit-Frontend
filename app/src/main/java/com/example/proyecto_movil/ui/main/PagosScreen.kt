package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.PagoResponse
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(navController: NavController) {
    var pagos by remember { mutableStateOf<List<PagoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = RetrofitClient.api.getPagos(Session.bearer())
                if (resp.isSuccessful) pagos = resp.body() ?: emptyList()
                else errorMessage = manejarError(resp.code(), navController)
            } catch (e: Exception) {
                errorMessage = "⚠️ Error de conexión"
            } finally { isLoading = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pagos", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_pago") }) {
                        Icon(Icons.Default.Add, contentDescription = "Registrar Pago", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZonaFitYellow)
            )
        },
        containerColor = ZonaFitDark
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ZonaFitYellow)
                errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                pagos.isEmpty() -> Text("No hay pagos registrados", color = Color.White, modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pagos) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${p.nombre ?: ""} ${p.apellido ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("$${p.monto} — ${p.metodo_pago}", color = Color.LightGray, fontSize = 13.sp)
                                Text(p.fecha_pago, color = ZonaFitYellow, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}