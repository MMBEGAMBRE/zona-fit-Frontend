package com.example.proyecto_movil.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_movil.data.LoginRequest
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    
    // --- ESTADO de la pantalla ---
    var email by remember { mutableStateOf("admin@zonafit.com") }
    var password by remember { mutableStateOf("admin123") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // TÍTULO PROFESIONAL
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = ZonaFitYellow)) { append("INICIA SESIÓN ") }
                        withStyle(style = SpanStyle(color = Color.White)) { append("EN TU\n") }
                        withStyle(style = SpanStyle(color = Color.White)) { append("GIMNASIO DE FORMA\n") }
                        withStyle(style = SpanStyle(color = ZonaFitYellow)) { append("INTELIGENTE") }
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Accede a tu panel de control para gestionar\nclientes, membresías y pagos.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            // CAMPOS DE ACCESO PRIVADO
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    placeholder = { Text("ejemplo@correo.com", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZonaFitYellow,
                        unfocusedBorderColor = ZonaFitYellow,
                        focusedLabelColor = ZonaFitYellow,
                        unfocusedLabelColor = ZonaFitYellow,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZonaFitYellow,
                        unfocusedBorderColor = ZonaFitYellow,
                        focusedLabelColor = ZonaFitYellow,
                        unfocusedLabelColor = ZonaFitYellow,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            result = "Escribe email y contraseña"
                            return@Button
                        }
                        loading = true
                        result = ""
                        
                        scope.launch {
                            try {
                                val response = RetrofitClient.api.login(LoginRequest(email, password))
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    Session.token = body?.token
                                    Session.userName = body?.user?.nombre
                                    Session.isAdmin = body?.user?.rol == "ADMINISTRADOR"
                                    onLoginSuccess()
                                } else {
                                    // Mensaje más detallado para depurar
                                    result = "Error ${response.code()}: Acceso denegado"
                                }
                            } catch (e: Exception) {
                                result = "⚠️ Sin conexión"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text(text = "INICIAR SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                if (result.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = result, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = "GESTIÓN DE GIMNASIO",
                    color = ZonaFitYellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SIMPLIFICADA",
                    color = ZonaFitYellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
