package com.example.tarea_082426.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
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
import com.example.tarea_082426.ui.censo.CensoScreen
import com.example.tarea_082426.ui.censo.CensoViewModel
import com.example.tarea_082426.ui.dashboard.DashboardScreen
import com.example.tarea_082426.ui.dashboard.DashboardViewModel
import com.example.tarea_082426.ui.map.MapScreen
import com.example.tarea_082426.ui.map.MapViewModel
import com.example.tarea_082426.ui.profile.ProfileScreen
import com.example.tarea_082426.ui.register.RegisterScreen
import com.example.tarea_082426.ui.register.RegisterViewModel

object AppRoutes { // Define las rutas (URLs internas) de la app
    const val LOGIN = "login"
    const val PROFILE = "profile/{id}/{token}/{nombre}/{apellido}/{usuario}"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard/{id}/{nombre}/{usuario}/{idRol}/{rol}"
    const val MAP = "map"
    const val CENSO = "censo"

    // Genera la ruta dinamica para perfil con sus argumentos
    fun profileRoute(id: Int, token: String, nombre: String, apellido: String, usuario: String): String {
        val encToken = Uri.encode(token)
        val encNombre = Uri.encode(nombre)
        val encApellido = Uri.encode(apellido)
        val encUsuario = Uri.encode(usuario)
        return "profile/$id/$encToken/$encNombre/$encApellido/$encUsuario"
    }

    fun dashboardRoute(id: Int, nombre: String, usuario: String, idRol: Int, rol: String): String {
        val encNombre = Uri.encode(nombre)
        val encUsuario = Uri.encode(usuario)
        val encRol = Uri.encode(rol)
        return "dashboard/$id/$encNombre/$encUsuario/$idRol/$encRol"
    }
}

@Composable
fun AppNavigator( // El "GPS" de la aplicacion
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
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
        composable(AppRoutes.REGISTER) {
            val viewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                },
                registerViewModel = viewModel
            )
        }
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
                onProfileClick = {
                    navController.navigate(AppRoutes.profileRoute(id, "", nombre, "", usuario))
                },
                onCensoClick = {
                    navController.navigate(AppRoutes.CENSO)
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onMapClick = {
                    navController.navigate(AppRoutes.MAP)
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.CENSO) {
            val viewModel: CensoViewModel = viewModel()
            CensoScreen(
                userId = id,
                onBackToDashboard = {
                    navController.popBackStack()
                },
                censoViewModel = viewModel
            )
        }
        composable(AppRoutes.MAP) {
            val viewModel: MapViewModel = viewModel()
            MapScreen(
                onBackToDashboard = {
                    navController.popBackStack()
                },
                mapViewModel = viewModel
            )
        }
        composable(
            route = AppRoutes.PROFILE,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("apellido") { type = NavType.StringType },
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val apellido = backStackEntry.arguments?.getString("apellido") ?: ""
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""

            ProfileScreen(
                id = id,
                nombre = nombre,
                apellido = apellido,
                usuario = usuario,
                onBackToDashboardClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
