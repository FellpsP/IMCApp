package com.example.imc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.imc.ui.Screen
import com.example.imc.ui.screen.DetailScreen
import com.example.imc.ui.screen.HistoryScreen
import com.example.imc.ui.screen.MainScreen
import com.example.imc.ui.theme.IMCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IMCTheme {
                HealthCalculatorApp()
            }
        }
    }
}

/**
 * Componente raiz da aplicação que gerencia a navegação.
 * Usamos NavHost para controlar qual tela está visível.
 */
@Composable
fun HealthCalculatorApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // NavController é o controlador de navegação
        val navController = rememberNavController()

        // NavHost é o container que gerencia as telas
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route  // Tela inicial
        ) {
            // Tela Principal
            composable(Screen.Main.route) {
                MainScreen(
                    onNavigateToHistory = {
                        // Navega para a tela de histórico
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            // Tela de Histórico
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateBack = {
                        // Volta para a tela anterior
                        navController.popBackStack()
                    },
                    onNavigateToDetail = { recordId ->
                        // Navega para detalhes com o ID do registro
                        navController.navigate(Screen.Detail.createRoute(recordId))
                    }
                )
            }

            // Tela de Detalhes
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("recordId") {
                        type = NavType.LongType  // Tipo do parâmetro
                        defaultValue = 0L  // Valor padrão
                    }
                )
            ) { backStackEntry ->
                // Extrai o parâmetro da rota
                val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L

                DetailScreen(
                    recordId = recordId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthCalculatorAppPreview() {
    IMCTheme {
        HealthCalculatorApp()
    }
}