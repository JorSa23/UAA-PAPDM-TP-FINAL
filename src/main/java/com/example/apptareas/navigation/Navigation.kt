package com.example.apptareas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.apptareas.detail.Examenes.ExamenScreen
import com.example.apptareas.detail.Examenes.ExamenViewModel
import com.example.apptareas.detail.TareasFacultad.TareasFacultadScreen
import com.example.apptareas.detail.TareasFacultad.TareasFacultadViewModel
import com.example.apptareas.home.Home
import com.example.apptareas.home.HomeViewMode
import com.example.apptareas.login.LoginScreen
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.login.SignUpScreen

enum class LoginRoutes {
    Signup,
    SignIn
}

enum class HomeRoutes {
    Home,
    ExamenDetail,
    TareaFacultadDetail
}

enum class NestedRoutes {
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    examenViewModel: ExamenViewModel,
    tareaFacultadViewModel: TareasFacultadViewModel,
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
            tareaFacultadViewModel = tareaFacultadViewModel,
            homeViewMode = homeViewMode
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        launchSingleTop = true
                        popUpTo(route = LoginRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.Signup.name)
            }
        }

        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(
                onNavToHomePage = {
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
    tareaFacultadViewModel: TareasFacultadViewModel, // ViewModel para TareasFacultad
    homeViewMode: HomeViewMode
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        // Pantalla Home
        composable(HomeRoutes.Home.name) {
            Home(
                homeViewMode = homeViewMode,
                onExamenClick = { examenId ->
                    navController.navigate(
                        HomeRoutes.ExamenDetail.name + "?id=$examenId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onTareaFacultadClick = { tareaId ->
                    navController.navigate(
                        HomeRoutes.TareaFacultadDetail.name + "?id=$tareaId"
                    ) {
                        launchSingleTop = true
                    }
                },
                navToExamenPage = {
                    navController.navigate(HomeRoutes.ExamenDetail.name)
                },
                navToTareaFacultadPage = { // Navegación para agregar una nueva tarea
                    navController.navigate(HomeRoutes.TareaFacultadDetail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }

        // Pantalla Detalle Examen
        composable(
            route = HomeRoutes.ExamenDetail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ExamenScreen(
                examenViewModel = examenViewModel,
                examenId = entry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }

        // Pantalla Detalle TareasFacultad
        composable(
            route = HomeRoutes.TareaFacultadDetail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            TareasFacultadScreen(
                TareasFacultadViewModel = tareaFacultadViewModel,
                TareasFacultadId = entry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }
    }
}
