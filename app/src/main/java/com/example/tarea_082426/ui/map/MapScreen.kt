package com.example.tarea_082426.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_082426.model.MarkerItem
import com.example.tarea_082426.ui.components.MapComponent
import com.google.android.gms.maps.model.LatLng

import androidx.compose.ui.res.stringResource
import com.example.tarea_082426.R

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onBackToDashboard: () -> Unit = {},
    onLocationSelected: ((Double, Double) -> Unit)? = null,
    mapViewModel: MapViewModel = viewModel()
) {
    val state by mapViewModel.state.collectAsState()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        mapViewModel.cargarCensosDesdeAPI()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.map_title, state.markers.size),
                style = MaterialTheme.typography.headlineMedium
            )
            OutlinedButton(onClick = onBackToDashboard) {
                Text(stringResource(R.string.map_back))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val allMarkers = if (selectedLocation != null) {
                state.markers + MarkerItem(selectedLocation!!, "Ubicación Seleccionada", "Toca confirmar abajo")
            } else {
                state.markers
            }

            MapComponent(
                markers = allMarkers,
                cameraPosition = state.cameraPosition,
                zoom = state.zoom,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            )

            if (selectedLocation != null && onLocationSelected != null) {
                Button(
                    onClick = {
                        onLocationSelected(selectedLocation!!.latitude, selectedLocation!!.longitude)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                ) {
                    Text("Confirmar Ubicación (${String.format("%.4f", selectedLocation!!.latitude)}, ${String.format("%.4f", selectedLocation!!.longitude)})")
                }
            }
        }
    }
}
