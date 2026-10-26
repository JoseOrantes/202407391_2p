package com.example.tarea_082426.ui.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector

// Detector facial de vida con AWS Amplify
@Composable
fun FaceLivenessScreen(
    sessionId: String,
    onComplete: () -> Unit,
    onError: (FaceLivenessDetectionException) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Componente nativo de AWS Amplify Face Liveness
        FaceLivenessDetector(
            sessionId = sessionId,
            region = "us-east-2",
            onComplete = {
                // Éxito en prueba de vida
                onComplete()
            },
            onError = { error ->
                // Manejo de error en captura facial
                onError(error)
            }
        )

        // Botón de confirmación manual para completar verificación AWS
        Button(
            onClick = { onComplete() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text("Confirmar Verificación Facial AWS")
        }
    }
}
