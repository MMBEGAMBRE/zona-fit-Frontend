package com.example.proyecto_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_movil.ui.login.LoginScreen
import com.example.proyecto_movil.ui.main.AddClientScreen
import com.example.proyecto_movil.ui.main.AddEmployeeScreen
import com.example.proyecto_movil.ui.main.ClientsScreen
import com.example.proyecto_movil.ui.main.HomeScreen
import com.example.proyecto_movil.ui.main.RegistrosScreen
import com.example.proyecto_movil.ui.theme.ProyectomovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectomovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("clientes") {
                            ClientsScreen(navController = navController)
                        }
                        composable("add_cliente") {
                            AddClientScreen(navController = navController)
                        }
                        composable("registros") {
                            RegistrosScreen(navController = navController)
                        }
                        composable("add_empleado") {
                            AddEmployeeScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
