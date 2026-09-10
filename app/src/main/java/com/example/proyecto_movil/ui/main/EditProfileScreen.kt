package com.example.proyecto_movil.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto_movil.data.RetrofitClient
import com.example.proyecto_movil.data.Session
import com.example.proyecto_movil.data.UpdateProfileRequest
import com.example.proyecto_movil.ui.theme.ZonaFitDark
import com.example.proyecto_movil.ui.theme.ZonaFitYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Precarga los datos actuales al entrar (una sola vez)
    LaunchedEffect(Unit) {
        try {
            val resp = RetrofitClient.api.getProfile(Session.bearer())
            if (resp.isSuccessful) {
                nombre = resp.body()?.user?.nombre ?: ""
                email = resp.body()?.user?.email ?: ""
            }
        } catch (_: Exception) { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar mis datos", fontWeight = FontWeight.Bold, color = Color.Black) },
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
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            CustomOutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
            Spacer(Modifier.height(12.dp))
            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Correo")
            Spacer(Modifier.height(24.dp))

            if (message.isNotEmpty()) {
                Text(message, color = if (message.contains("✅")) Color.Green else Color.Red, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    loading = true; message = ""
                    scope.launch {
                        try {
                            val resp = RetrofitClient.api.updateProfile(
                                Session.bearer(), UpdateProfileRequest(nombre, email)
                            )
                            if (resp.isSuccessful) {
                                Session.userName = resp.body()?.user?.nombre
                                message = "✅ Datos actualizados"
                            } else if (resp.code() == 409) {
                                message = "❌ Ese correo ya está en uso"
                            } else {
                                message = "❌ Error (código ${resp.code()})"
                            }
                        } catch (e: Exception) {
                            message = "⚠️ ${e.message}"
                        } finally { loading = false }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZonaFitYellow)
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                else Text("GUARDAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
