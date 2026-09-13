package com.pandora.app.navigation

sealed class Screen(val route: String) {
    data object Timeline : Screen("timeline")
    data object Organize : Screen("organize")
    data object Search : Screen("search")
    data object Explore : Screen("explore")
    data object Settings : Screen("settings")
    data object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Long): String = "item_detail/$itemId"
    }
}
