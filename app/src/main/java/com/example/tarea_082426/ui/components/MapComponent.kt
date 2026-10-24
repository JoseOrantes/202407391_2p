package com.example.tarea_082426.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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
        markers.forEach { marker ->
            val markerState = remember(marker.position) { MarkerState(position = marker.position) }
            Marker(
                state = markerState,
                title = marker.title,
                snippet = marker.snippet,
                onClick = {
                    onMarkerClick(marker)
                    false
                }
            )
        }
    }
}
