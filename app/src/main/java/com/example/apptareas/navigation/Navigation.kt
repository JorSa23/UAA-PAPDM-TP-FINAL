package com.example.apptareas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import com.example.apptareas.login.LoginScreen
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.login.SignUpScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.apptareas.ComprasHome.ComprasHomeScreen
import com.example.apptareas.ComprasHome.ComprasHomeViewModel
import com.example.apptareas.detail.Compras.ComprasScreen
import com.example.apptareas.detail.Compras.ComprasViewModel
import com.example.apptareas.detail.Examenes.ExamenScreen
import com.example.apptareas.detail.Examenes.ExamenViewModel
import com.example.apptareas.home.Home
import com.example.apptareas.home.HomeViewMode


enum class LoginRoutes{
    Signup,
    SignIn
}
enum class HomeRoutes {
    Home,
    Detail,
    ComprasHome,
    Compras
}

enum class NestedRoutes{
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    examenViewModel: ExamenViewModel,
    comprasHomeViewModel: ComprasHomeViewModel,
    comprasViewModel: ComprasViewModel,
    homeViewMode: HomeViewMode
) {
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Main.name
    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController = navController,
            examenViewModel = examenViewModel,
            comprasHomeViewModel = comprasHomeViewModel,
            comprasViewModel = comprasViewModel,
            homeViewMode = homeViewMode
        )
    }
}


fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
){
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ){
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel

            ) {
                navController.navigate(LoginRoutes.Signup.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    popUpTo(LoginRoutes.Signup.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel

            ) {
                navController.navigate(LoginRoutes.SignIn.name)

            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    examenViewModel: ExamenViewModel,
    comprasHomeViewModel: ComprasHomeViewModel,
    comprasViewModel: ComprasViewModel,
    homeViewMode: HomeViewMode
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        // Pantalla principal
        composable(HomeRoutes.Home.name) {
            Home(
                homeViewMode = homeViewMode,
                onExamenClick = { examenId ->
                    navController.navigate(HomeRoutes.Detail.name + "?id=$examenId") {
                        launchSingleTop = true
                    }
                },
                navToExamenPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                },
                navToComprasPage = {
                    navController.navigate(HomeRoutes.ComprasHome.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        // Detalle de Examen
        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ExamenScreen(
                examenViewModel = examenViewModel,
                examenId = entry.arguments?.getString("id") as String
            ) {
                navController.navigateUp()
            }
        }

        // Pantalla principal de Compras
        composable(route = HomeRoutes.ComprasHome.name) {
            ComprasHomeScreen(
                comprashomeViewModel = comprasHomeViewModel,

                onComprasClick = { comprasId ->
                    navController.navigate(HomeRoutes.Compras.name + "?id=$comprasId")
                },
                navToComprasHomePage = {
                    navController.navigate(HomeRoutes.ComprasHome.name)
                },
                navToExamenPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                },
                navToComprasPage = {
                    navController.navigate(HomeRoutes.Compras.name)
                },
                navToLoginPage = {
                    navController.navigate(NestedRoutes.Login.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Detalle de Compras
        composable(
            route = HomeRoutes.Compras.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ComprasScreen(
                comprasViewModel = comprasViewModel,
                comprasId = entry.arguments?.getString("id") as String
            ) {
                navController.navigateUp()
            }
        }
    }
}
