package com.example.tarea_082426.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    // Estado de marcadores y ubicación elegida
    val state by mapViewModel.state.collectAsState()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // Variable para recordad marcador seleccionado
    var clickedMarker by remember { mutableStateOf<MarkerItem?>(null) }

    val isSelectingLocation = onLocationSelected != null

    // Carga de censos desde API únicamente para Administrador
    LaunchedEffect(isSelectingLocation) {
        if (!isSelectingLocation) {
            mapViewModel.cargarCensosDesdeAPI()
        } else {
            mapViewModel.limpiarMarcadores()
        }
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
            // Modo selección únicamente para Censista
            val isSelectingLocation = onLocationSelected != null

            // Lista de marcadores más selección activa
            val allMarkers = if (isSelectingLocation && selectedLocation != null) {
                state.markers + MarkerItem(selectedLocation!!, "Selected Location", "")
            } else {
                state.markers
            }

            MapComponent(
                markers = allMarkers,
                cameraPosition = state.cameraPosition,
                zoom = state.zoom,
                onMapClick = { latLng ->
                    if (isSelectingLocation) {
                        selectedLocation = latLng
                    }
                },
                onMarkerClick = { marker ->
                    if (!isSelectingLocation) {
                        clickedMarker = marker
                    }
                }
            )

            // Botón de confirmación de coordenadas para Censista
            if (onLocationSelected != null && selectedLocation != null) {
                Button(
                    onClick = {
                        onLocationSelected(selectedLocation!!.latitude, selectedLocation!!.longitude)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                ) {
                    Text(stringResource(R.string.map_confirm_location, selectedLocation!!.latitude, selectedLocation!!.longitude))
                }
            }
            // Diálogo de Material 3 con detalles de la ubicación seleccionada al tocar un marcador
            if (clickedMarker != null) {
                AlertDialog(
                    onDismissRequest = { clickedMarker = null },
                    title = {
                        Text(
                            text = "Name: ${clickedMarker!!.title}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Description: ${clickedMarker!!.snippet}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Location: Lat ${String.format("%.4f", clickedMarker!!.position.latitude)}, Lng ${String.format("%.4f", clickedMarker!!.position.longitude)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { clickedMarker = null }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
