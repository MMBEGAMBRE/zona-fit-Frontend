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
import com.example.proyecto_movil.data.MembresiaResponse
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembresiasScreen(navController: NavController) {
    var membresias by remember { mutableStateOf<List<MembresiaResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = RetrofitClient.api.getMembresias(Session.bearer())
                if (resp.isSuccessful) membresias = resp.body() ?: emptyList()
                else errorMessage = "Error al obtener membresías (código ${resp.code()})"
            } catch (e: Exception) {
                errorMessage = "⚠️ Error de conexión"
            } finally { isLoading = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membresías", fontWeight = FontWeight.Bold, color = Color.Black) },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ZonaFitYellow)
                errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                membresias.isEmpty() -> Text("No hay membresías registradas", color = Color.White, modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(membresias) { m ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${m.cliente_nombre ?: ""} ${m.cliente_apellido ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Plan: ${m.tipo}", color = Color.LightGray, fontSize = 13.sp)
                                Text("Vence: ${m.fecha_vencimiento}", color = Color.LightGray, fontSize = 13.sp)
                                Text(m.estado, color = ZonaFitYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
