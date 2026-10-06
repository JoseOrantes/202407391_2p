package com.example.tarea_082426.model

import com.google.android.gms.maps.model.LatLng

data class MarkerItem(
    val position: LatLng,
    val title: String = "",
    val snippet: String = ""
)

data class MapState(
    val markers: List<MarkerItem> = emptyList(),
    val cameraPosition: LatLng = LatLng(14.4833, -90.5353),
    val zoom: Float = 6f
)
