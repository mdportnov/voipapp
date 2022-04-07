package ru.mephi.voip.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.mephi.voip.ui.caller.CallerScreen
import ru.mephi.voip.ui.catalog.CatalogScreen
import ru.mephi.voip.ui.profile.ProfileScreen

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MyNavHost(navController: NavHostController, starDest: String) {
    NavHost(navController = navController, startDestination = starDest) {
        composable(route = "CallerScreen") {
            CallerScreen(navController = navController)
        }
        composable(route = "CatalogScreen") {
            CatalogScreen(navController)
        }
        composable(route = "ProfileScreen") {
            ProfileScreen(navController)
        }
    }
}