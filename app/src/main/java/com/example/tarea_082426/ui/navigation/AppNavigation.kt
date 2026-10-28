package com.example.tarea_082426.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tarea_082426.ui.Login.LoginScreen
import com.example.tarea_082426.ui.Login.LoginViewModel
import com.example.tarea_082426.ui.dashboard.DashboardScreen
import com.example.tarea_082426.ui.location.LocationListScreen
import com.example.tarea_082426.ui.location.LocationListViewModel
import com.example.tarea_082426.ui.location.LocationScreen
import com.example.tarea_082426.ui.location.LocationViewModel
import com.example.tarea_082426.ui.map.MapScreen
import com.example.tarea_082426.ui.map.MapViewModel

object AppRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard/{id}/{nombre}/{usuario}/{idRol}/{rol}"
    const val MAP = "map/{userId}"
    const val LOCATION = "location/{userId}"
    const val LOCATION_LIST = "location_list/{userId}"

    fun locationRoute(userId: Int): String {
        return "location/$userId"
    }

    fun locationListRoute(userId: Int): String {
        return "location_list/$userId"
    }

    fun mapRoute(userId: Int): String {
        return "map/$userId"
    }

    fun dashboardRoute(id: Int, nombre: String, usuario: String, idRol: Int, rol: String): String {
        val encNombre = Uri.encode(nombre)
        val encUsuario = Uri.encode(usuario)
        val encRol = Uri.encode(rol)
        return "dashboard/$id/$encNombre/$encUsuario/$idRol/$encRol"
    }
}

@Composable
fun AppNavigator(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
        // Pantalla de inicio de sesión
        composable(AppRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                loginViewModel = viewModel,
                onLoginSuccess = { id, token, nombre, apellido, usuario, idRol, rol ->
                    navController.navigate(AppRoutes.dashboardRoute(id, nombre, usuario, idRol, rol)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                modifier = Modifier
            )
        }
        // Menú principal
        composable(
            route = AppRoutes.DASHBOARD,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("usuario") { type = NavType.StringType },
                navArgument("idRol") { type = NavType.IntType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            val idRol = backStackEntry.arguments?.getInt("idRol") ?: 2
            val rol = backStackEntry.arguments?.getString("rol") ?: ""

            DashboardScreen(
                id = id,
                nombre = nombre,
                usuario = usuario,
                idRol = idRol,
                rol = rol,
                onCensoClick = {
                    navController.navigate(AppRoutes.locationRoute(id))
                },
                onListClick = {
                    navController.navigate(AppRoutes.locationListRoute(id))
                },
                onMapClick = {
                    navController.navigate(AppRoutes.mapRoute(id))
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        // Captura de ubicación
        composable(
            route = AppRoutes.LOCATION,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val viewModel: LocationViewModel = viewModel()

            val savedStateHandle = backStackEntry.savedStateHandle
            val selectedLat = savedStateHandle.getStateFlow<Double?>("selected_lat", null).collectAsState()
            val selectedLng = savedStateHandle.getStateFlow<Double?>("selected_lng", null).collectAsState()

            LaunchedEffect(selectedLat.value, selectedLng.value) {
                if (selectedLat.value != null && selectedLng.value != null) {
                    viewModel.onUbicacionSeleccionada(selectedLat.value!!, selectedLng.value!!)
                    savedStateHandle.remove<Double>("selected_lat")
                    savedStateHandle.remove<Double>("selected_lng")
                }
            }

            LocationScreen(
                userId = userId,
                onBackToDashboard = {
                    navController.popBackStack()
                },
                onMapClick = {
                    navController.navigate(AppRoutes.mapRoute(userId))
                },
                locationViewModel = viewModel
            )
        }
        // Pantalla de listado de ubicaciones guardadas
        composable(
            route = AppRoutes.LOCATION_LIST,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val viewModel: LocationListViewModel = viewModel()

            LocationListScreen(
                userId = userId,
                onBackToDashboard = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
        // Pantalla de mapa
        composable(
            route = AppRoutes.MAP,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val viewModel: MapViewModel = viewModel()
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val isFromLocation = previousRoute?.startsWith("location") == true

            MapScreen(
                userId = userId,
                onBackToDashboard = {
                    navController.popBackStack()
                },
                onLocationSelected = if (isFromLocation) { lat, lng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_lat", lat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_lng", lng)
                    navController.popBackStack()
                } else null,
                mapViewModel = viewModel
            )
        }
    }
}
