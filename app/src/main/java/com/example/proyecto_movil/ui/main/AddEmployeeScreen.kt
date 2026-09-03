package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.proyecto_movil.data.RegisterStaffRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("EMPLEADO") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contratar Personal", color = Color.Black, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro de Nuevo Trabajador",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            CustomOutlinedTextField(value = name, onValueChange = { name = it }, label = "Nombre del Empleado")
            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Correo Corporativo")
            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(value = password, onValueChange = { password = it }, label = "Contraseña Temporal")
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("Tipo de Cuenta:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = rol == "EMPLEADO",
                    onClick = { rol = "EMPLEADO" },
                    colors = RadioButtonDefaults.colors(selectedColor = ZonaFitYellow, unselectedColor = Color.Gray)
                )
                Text("Empleado", color = Color.White)
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = rol == "ADMINISTRADOR",
                    onClick = { rol = "ADMINISTRADOR" },
                    colors = RadioButtonDefaults.colors(selectedColor = ZonaFitYellow, unselectedColor = Color.Gray)
                )
                Text("Administrador", color = Color.White)
            }

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
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        message = "⚠️ Todos los campos son obligatorios"
                        return@Button
                    }
                    
                    isLoading = true
                    message = ""
                    
                    scope.launch {
                        try {
                            val request = RegisterStaffRequest(name, email, password, rol)
                            val response = RetrofitClient.api.registerStaff(Session.bearer(), request)
                            
                            if (response.isSuccessful) {
                                message = "✅ Personal registrado con éxito"
                                kotlinx.coroutines.delay(1500)
                                navController.popBackStack()
                            } else {
                                message = "❌ Error: Correo ya registrado o sin permisos"
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
                    Text("CREAR CUENTA", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
