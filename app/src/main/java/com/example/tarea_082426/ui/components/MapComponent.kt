package com.example.tarea_082426.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapComponent(
    markers: List<MarkerItem>,
    cameraPosition: LatLng,
    zoom: Float,
    modifier: Modifier = Modifier,
    onMarkerClick: (MarkerItem) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {}
) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, zoom)
    }

    LaunchedEffect(cameraPosition, zoom) {
        cameraState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(cameraPosition, zoom)
            )
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        onMapClick = onMapClick
    ) {
        Clustering(
            items = markers,
            onClusterItemClick = { item ->
                onMarkerClick(item)
                false
            },
            clusterItemContent = { item ->
                val pinColor = if (item.isMasivo) Color(0xFF1E88E5) else Color(0xFFE53935)

                Canvas(
                    modifier = Modifier.size(28.dp, 40.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    val pinPath = Path().apply {
                        moveTo(w / 2f, h)
                        cubicTo(w * 0.12f, h * 0.58f, 0f, h * 0.40f, 0f, w / 2f)
                        arcTo(
                            rect = Rect(0f, 0f, w, w),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = 180f,
                            forceMoveTo = false
                        )
                        cubicTo(w, h * 0.40f, w * 0.88f, h * 0.58f, w / 2f, h)
                        close()
                    }

                    // Cuerpo del pin en el color seleccionado (Rojo o Azul)
                    drawPath(path = pinPath, color = pinColor)

                    // Círculo centro blanco estándar del pin
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.20f,
                        center = Offset(w / 2f, w / 2f)
                    )
                }
            }
        )
    }
}
