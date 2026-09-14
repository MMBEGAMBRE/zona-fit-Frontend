package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // Reto GA2-AA4-EV04 #1: confirmar antes de cerrar sesión (AlertDialog)
    var mostrarConfirmacionSalida by remember { mutableStateOf(false) }
    var menuPerfilAbierto by remember { mutableStateOf(false) }

    if (mostrarConfirmacionSalida) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas salir?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacionSalida = false
                    Session.clear()
                    navController.navigate("login") { popUpTo(0) }
                }) {
                    Text("Salir", color = ZonaFitYellow, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionSalida = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                actions = {
                    Box {
                        IconButton(onClick = { menuPerfilAbierto = true }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.Black)
                        }
                        DropdownMenu(
                            expanded = menuPerfilAbierto,
                            onDismissRequest = { menuPerfilAbierto = false },
                            modifier = Modifier.background(ZonaFitDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mis Datos", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ZonaFitYellow) },
                                onClick = {
                                    menuPerfilAbierto = false
                                    navController.navigate("editProfile")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Seguridad", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ZonaFitYellow) },
                                onClick = {
                                    menuPerfilAbierto = false
                                    navController.navigate("changePassword")
                                }
                            )
                            HorizontalDivider(color = Color.Gray)
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color.Red) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    menuPerfilAbierto = false
                                    mostrarConfirmacionSalida = true
                                }
                            )
                        }
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
                text = "ADMINISTRACIÓN DEL GIMNASIO",
                color = ZonaFitYellow,
                fontSize = 20.sp,
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
                text = "Acceso: ${if (Session.isAdmin) "ADMINISTRADOR" else "OPERATIVO"}",
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
                // OPCIÓN PARA TODOS: Clientes (Socio)
                item {
                    PremiumMenuCard(
                        title = "Socios",
                        description = "Registro y listado de clientes del gimnasio.",
                        icon = Icons.Default.Group,
                        onClick = { navController.navigate("clientes") }
                    )
                }

                // OPCIÓN PARA TODOS: Membresías
                item {
                    PremiumMenuCard(
                        title = "Membresías",
                        description = "Asignar planes y verificar vencimientos.",
                        icon = Icons.Default.Badge,
                        onClick = { navController.navigate("membresias") }
                    )
                }

                // --- OPCIONES EXCLUSIVAS PARA ADMINISTRADOR ---
                if (Session.isAdmin) {
                    item {
                        PremiumMenuCard(
                            title = "Personal",
                            description = "Gestión de empleados y permisos.",
                            icon = Icons.Default.Engineering,
                            onClick = { navController.navigate("empleados") }
                        )
                    }
                    item {
                        PremiumMenuCard(
                            title = "Auditoría",
                            description = "Historial técnico del sistema.",
                            icon = Icons.Default.DoorSliding,
                            onClick = { navController.navigate("registros") }
                        )
                    }
                }
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
