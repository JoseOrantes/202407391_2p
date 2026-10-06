package com.example.tarea_082426.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    mapViewModel: MapViewModel = viewModel()
) {
    val state by mapViewModel.state.collectAsState()

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
            MapComponent(
                markers = state.markers,
                cameraPosition = state.cameraPosition,
                zoom = state.zoom
            )
        }
    }
}
