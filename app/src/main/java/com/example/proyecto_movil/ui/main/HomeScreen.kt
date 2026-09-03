package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ZONA FIT EVOLUTION",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ZonaFitYellow
                )
            )
        },
        containerColor = ZonaFitDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BANNER SUPERIOR
            Text(
                text = "TODO LO QUE NECESITAS PARA ADMINISTRAR TU GIMNASIO",
                color = ZonaFitYellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Hola, ${Session.userName ?: "Staff"} 👋",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            // INDICADOR DE ROL
            Text(
                text = "Rol: ${if (Session.isAdmin) "ADMINISTRADOR" else "EMPLEADO"}",
                fontSize = 12.sp,
                color = ZonaFitYellow,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Cuadrícula dinámica según Rol
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PremiumMenuCard(
                        title = "Gestión de Clientes",
                        description = "Registra y administra la información de tus socios.",
                        icon = Icons.Default.Group,
                        onClick = { navController.navigate("clientes") }
                    )
                }
                item {
                    PremiumMenuCard(
                        title = "Membresías",
                        description = "Controla planes, pagos y fechas de vencimiento.",
                        icon = Icons.Default.Badge,
                        onClick = { /* Navigate to Membresias */ }
                    )
                }
                item {
                    PremiumMenuCard(
                        title = "Pagos",
                        description = "Registro de ingresos y consulta de historial.",
                        icon = Icons.Default.AttachMoney,
                        onClick = { /* Navigate to Pagos */ }
                    )
                }
                
                // OPCIONES EXCLUSIVAS PARA ADMINISTRADOR (Dueño)
                if (Session.isAdmin) {
                    item {
                        PremiumMenuCard(
                            title = "Gestión Empleados",
                            description = "Administra el personal y asigna permisos.",
                            icon = Icons.Default.Engineering,
                            onClick = { navController.navigate("add_empleado") }
                        )
                    }
                    item {
                        PremiumMenuCard(
                            title = "Control Accesos",
                            description = "Historial de actividad y registros del sistema.",
                            icon = Icons.Default.DoorSliding,
                            onClick = { navController.navigate("registros") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Session.clear()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZonaFitYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CERRAR SESIÓN", color = ZonaFitYellow, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PremiumMenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = ZonaFitYellow
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}
