package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.RegistroResponse
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrosScreen(navController: NavController) {
    var registros by remember { mutableStateOf<List<RegistroResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getRegistros(Session.bearer())
                if (response.isSuccessful) {
                    registros = response.body() ?: emptyList()
                } else {
                    errorMessage = "No tienes permiso o el servidor falló"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Accesos", fontWeight = FontWeight.Bold, color = Color.Black) },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ZonaFitYellow)
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (registros.isEmpty()) {
                Text(text = "No hay registros de actividad", color = Color.White, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(registros) { reg ->
                        RegistroItem(reg)
                    }
                }
            }
        }
    }
}

@Composable
fun RegistroItem(reg: RegistroResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = ZonaFitYellow, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = reg.fecha_hora, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(text = reg.accion, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Text(text = reg.descripcion, color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Usuario: ${reg.usuario_nombre ?: "Sistema"} (${reg.usuario_rol ?: "N/A"})",
                color = ZonaFitYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
