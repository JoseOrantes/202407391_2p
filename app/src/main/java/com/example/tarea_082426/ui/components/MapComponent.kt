package com.example.tarea_082426.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState

// Renderer de agrupamiento nativo (Todos los marcadores en color rojo estándar)
class CustomClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MarkerItem>
) : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: MarkerItem, markerOptions: MarkerOptions) {
        // Todos los marcadores rojos según el estándar
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        markerOptions.title(item.title)
        markerOptions.snippet(item.snippet)
    }

    override fun onClusterItemUpdated(item: MarkerItem, marker: Marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        marker.title = item.title
        marker.snippet = item.snippet
    }
}

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
    val context = LocalContext.current
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

    var clusterManager by remember { mutableStateOf<ClusterManager<MarkerItem>?>(null) }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        onMapClick = onMapClick
    ) {
        MapEffect(markers) { map ->
            if (clusterManager == null) {
                val manager = ClusterManager<MarkerItem>(context, map)
                val renderer = CustomClusterRenderer(context, map, manager)
                manager.renderer = renderer

                map.setOnCameraIdleListener(manager)
                map.setOnMarkerClickListener(manager)

                manager.setOnClusterItemClickListener { item ->
                    onMarkerClick(item)
                    false
                }
                clusterManager = manager
            }

            clusterManager?.clearItems()
            clusterManager?.addItems(markers)
            clusterManager?.cluster()
        }
    }
}
