package com.example.imc.ui

/**
 * Objeto selado que define todas as telas da aplicação.
 * Usamos sealed class para ter um conjunto fechado de rotas.
 */
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object History : Screen("history")
    object Detail : Screen("detail/{recordId}") {
        // Função auxiliar para criar rota com parâmetro
        fun createRoute(recordId: Long) = "detail/$recordId"
    }

    // Podemos adicionar mais telas futuramente
    // object Help : Screen("help")

}