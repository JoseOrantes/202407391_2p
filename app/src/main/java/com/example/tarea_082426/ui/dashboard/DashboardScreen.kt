package com.example.tarea_082426.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.res.stringResource
import com.example.tarea_082426.R

@Composable
fun DashboardScreen(
    id: Int,
    nombre: String,
    usuario: String,
    idRol: Int,
    rol: String,
    onProfileClick: () -> Unit = {},
    onCensoClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    // Es Admin si idRol es 1 o el string es "ADMIN"
    val esAdmin = idRol == 1 || rol.equals("ADMIN", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.dashboard_welcome, usuario),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val rolNombre = if (esAdmin) "ADMIN" else if (rol.isNotEmpty()) rol else "CENSISTA"
        Text(
            text = stringResource(R.string.dashboard_role, rolNombre),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.dashboard_main_menu),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Opcion comun: Ver Perfil
                Button(
                    onClick = onProfileClick,
                    modifier = Modifier.width(280.dp)
                ) {
                    Text(stringResource(R.string.dashboard_view_profile))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Opciones exclusivas para ADMIN (idRol == 1)
                if (esAdmin) {
                    Button(
                        onClick = onMapClick,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Text(stringResource(R.string.dashboard_map))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Text(stringResource(R.string.dashboard_register_user))
                    }
                } else {
                    // Opción exclusiva para CENSISTA / USER (idRol == 2)
                    Button(
                        onClick = onCensoClick,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Text(stringResource(R.string.dashboard_take_census))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.width(280.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(stringResource(R.string.dashboard_logout))
        }
    }
}
