package com.example.tarea_082426.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class MarkerItem(
    @get:JvmName("getMarkerPosition") val position: LatLng,
    @get:JvmName("getMarkerTitle") val title: String = "",
    @get:JvmName("getMarkerSnippet") val snippet: String = "",
    val isMasivo: Boolean = false
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = title
    override fun getSnippet(): String = snippet
    override fun getZIndex(): Float? = null
}

data class MapState(
    val markers: List<MarkerItem> = emptyList(),
    val cameraPosition: LatLng = LatLng(14.4833, -90.5353),
    val zoom: Float = 6f
)
