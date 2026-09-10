package com.example.proyecto_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_movil.ui.login.LoginScreen
import com.example.proyecto_movil.ui.main.AddClientScreen
import com.example.proyecto_movil.ui.main.AddEmployeeScreen
import com.example.proyecto_movil.ui.main.AddMembresiaScreen
import com.example.proyecto_movil.ui.main.AddPagoScreen
import com.example.proyecto_movil.ui.main.ChangePasswordScreen
import com.example.proyecto_movil.ui.main.ClientsScreen
import com.example.proyecto_movil.ui.main.EditClientScreen
import com.example.proyecto_movil.ui.main.EditProfileScreen
import com.example.proyecto_movil.ui.main.HomeScreen
import com.example.proyecto_movil.ui.main.MembresiasScreen
import com.example.proyecto_movil.ui.main.PagosScreen
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
                        composable("membresias") {
                            MembresiasScreen(navController = navController)
                        }
                        composable("add_membresia") {
                            AddMembresiaScreen(navController = navController)
                        }
                        composable("pagos") {
                            PagosScreen(navController = navController)
                        }
                        composable("add_pago") {
                            AddPagoScreen(navController = navController)
                        }
                        composable("editProfile") {
                            EditProfileScreen(navController = navController)
                        }
                        composable("changePassword") {
                            ChangePasswordScreen(navController = navController)
                        }
                        composable(
                            "edit_cliente/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val clienteId = backStackEntry.arguments?.getInt("id") ?: 0
                            EditClientScreen(navController = navController, clienteId = clienteId)
                        }
                    }
                }
            }
        }
    }
}
