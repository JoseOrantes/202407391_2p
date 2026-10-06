package com.example.tarea_082426.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.res.stringResource
import com.example.tarea_082426.R

@Composable
fun ProfileScreen(
    id: Int,
    nombre: String,
    apellido: String,
    usuario: String,
    onBackToDashboardClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state by profileViewModel.state.collectAsState()
    val context = LocalContext.current

    //Para lo de la foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            profileViewModel.onFotoTomada(bitmap, context)
        }
    }

    // Permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) cameraLauncher.launch(null)
    }

    // Dando opcion de gallery
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri? ->
        if (uri != null) {
            // Convirtiendo Uri a Bitmap
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            //Enviando bitmap a viewModel
            profileViewModel.onFotoTomada(bitmap, context)
        }
    }

    // Revisa los permisos del usuario antes de abrir la cámara
    fun abrirCamara() {
        val yaConcedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (yaConcedido) cameraLauncher.launch(null)
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(id) {
        profileViewModel.loadProfile(id, context)
    }

    // Usamos los datos del estado (de la DB) o los parámetros (de la navegación) como respaldo
    val displayNombre = if (state.nombre.isNotEmpty()) state.nombre else nombre
    val displayApellido = if (state.apellido.isNotEmpty()) state.apellido else apellido
    val displayUsuario = if (state.usuario.isNotEmpty()) state.usuario else usuario

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.profile_greeting),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                //abrirCamara()
                //Abre las opciones de gallery o camera
                mostrarOpcionesFoto = true
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.fotoPerfil != null){
                    Image(
                        bitmap = state.fotoPerfil!!.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "${displayNombre.firstOrNull() ?: ""}${displayApellido.firstOrNull() ?: ""}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "$displayNombre $displayApellido",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.width(600.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ProfileInfoRow(label = stringResource(R.string.profile_name), value = displayNombre)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_lastname), value = displayApellido)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_username), value = displayUsuario)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_email), value = state.correo)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_phone), value = state.telefono)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_birthdate), value = state.fechaNac)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(label = stringResource(R.string.profile_gender), value = state.genero)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onBackToDashboardClick,
            modifier = Modifier.width(300.dp)
        ) {
            Text(stringResource(R.string.profile_back_dashboard))
        }
    }
    //Dialogo de Alerta
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text(stringResource(R.string.profile_dialog_title)) },
            text = { Text(stringResource(R.string.profile_dialog_msg)) },
            confirmButton = {
                TextButton  (
                    onClick = {
                        mostrarOpcionesFoto = false
                        abrirCamara()
                    }
                ) {
                    Text(stringResource(R.string.profile_dialog_camera))
                }
            },
            dismissButton = {
                TextButton (
                    onClick = {
                        mostrarOpcionesFoto = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text(stringResource(R.string.profile_dialog_gallery))
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
