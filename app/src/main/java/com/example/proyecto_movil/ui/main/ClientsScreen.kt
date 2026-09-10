package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.proyecto_movil.data.ClienteResponse
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.manejarError
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(navController: NavController) {
    var clients by remember { mutableStateOf<List<ClienteResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getClientes(Session.bearer())
                if (response.isSuccessful) {
                    clients = response.body() ?: emptyList()
                } else {
                    errorMessage = manejarError(response.code(), navController)
                }
            } catch (e: Exception) {
                errorMessage = "📡 Sin conexión al servidor"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    // RESTRICCIÓN POR ROL (Alineado con Guía GA3 - Paso 3)
                    if (Session.isAdmin) {
                        IconButton(onClick = { navController.navigate("add_cliente") }) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo Cliente", tint = Color.Black)
                        }
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
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
            } else if (clients.isEmpty()) {
                Text(text = "No hay clientes registrados", color = Color.White, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clients) { cliente ->
                        ClientItem(cliente) { id -> navController.navigate("edit_cliente/$id") }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientItem(cliente: ClienteResponse, onEditClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditClick(cliente.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(ZonaFitYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cliente.nombre.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${cliente.nombre} ${cliente.apellido}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(text = "CC: ${cliente.documento}", fontSize = 12.sp, color = Color.LightGray)
                Text(text = cliente.email ?: "Sin correo", fontSize = 12.sp, color = Color.LightGray)
            }

            Surface(
                color = if (cliente.estado == "ACTIVO") Color(0xFF2E7D32) else Color(0xFFC62828),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = cliente.estado ?: "INACTIVO",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
