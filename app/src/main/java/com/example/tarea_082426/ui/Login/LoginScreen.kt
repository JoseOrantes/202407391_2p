package com.example.tarea_082426.ui.Login

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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

    // Launcher para tomar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            loginViewModel.onFacePhotoTaken(bitmap, context)
        }
    }

    // Launcher de permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) cameraLauncher.launch(null)
    }

    // Verifica permiso de cámara
    fun abrirCamaraParaLogin() {
        val yaConcedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (yaConcedido) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) {
            onLoginSuccess(state.id, state.token, state.nombre, state.apellido, state.usuario, state.id_rol, state.rol)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLivenessActive && state.livenessSessionId != null) {
            // Pantalla completa de verificación facial de vida AWS Amplify
            FaceLivenessScreen(
                sessionId = state.livenessSessionId!!,
                onComplete = {
                    loginViewModel.onLivenessCompleted(context)
                },
                onError = {
                    loginViewModel.closeLiveness()
                }
            )
        } else {
            // Formulario de inicio de sesión estándar
            Column(
                modifier = Modifier
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

                Card(
                    modifier = Modifier.width(360.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { loginViewModel.onEmailChange(it) },
                            label = { Text(stringResource(R.string.login_email)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { loginViewModel.onPasswordChange(it) },
                            label = { Text(stringResource(R.string.login_password)) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Validación sintáctica de correo
                        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()

                        Button(
                            onClick = {
                                loginViewModel.onLoginClick(context)
                            },
                            enabled = isEmailValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4997D0),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.login_button))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón para inicio de sesión con foto facial
                        Button(
                            onClick = {
                                abrirCamaraParaLogin()
                            },
                            enabled = isEmailValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4997D0),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.login_face_button))
                        }
                    }
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

        // Overlay de escaneo facial animado durante llamadas a AWS
        if (state.isLivenessLoading) {
            FaceScanningOverlay(
                bitmap = state.faceBitmap,
                statusMessage = state.mensaje
            )
        }
    }
}

// Overlay animado con la foto capturada y la barra de escaneo arcoíris
@Composable
fun FaceScanningOverlay(
    bitmap: Bitmap?,
    statusMessage: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Análisis Facial AWS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto capturada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    // Línea de escaneo animada con degradado arcoíris
                    val infiniteTransition = rememberInfiniteTransition(label = "scan")
                    val scanProgress by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "progress"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val y = size.height * scanProgress
                        val rainbowBrush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color(0xFFFF7F00),
                                Color.Yellow,
                                Color.Green,
                                Color.Blue,
                                Color(0xFF4B0082),
                                Color(0xFF8B0083)
                            )
                        )
                        drawLine(
                            brush = rainbowBrush,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 8f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = statusMessage.ifEmpty { "Verificando identidad con AWS Rekognition..." },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
