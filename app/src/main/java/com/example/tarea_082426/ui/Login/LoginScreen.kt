package com.example.tarea_082426.ui.Login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_082426.R

@Composable //Vuelve el siguiente codigo parte del UI
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(),
    //Preparo la data a recibir en un lambda (callback)
    onLoginSuccess: (id: Int, token: String, nombre: String, apellido: String, usuario: String, idRol: Int, rol: String) -> Unit = { _, _, _, _, _, _, _ -> }
) {

    val state by loginViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) {
            onLoginSuccess(state.id, state.token, state.nombre, state.apellido, state.usuario, state.id_rol, state.rol)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { loginViewModel.onEmailChange(it) },
            label = { Text(stringResource(R.string.login_email)) },
            modifier = Modifier.width(300.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text(stringResource(R.string.login_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.width(300.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.onLoginClick(context)
            },
            modifier = Modifier.width(300.dp)
        ) {
            Text(stringResource(R.string.login_button))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.mensaje,
            color = if (state.loginExitoso) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}
