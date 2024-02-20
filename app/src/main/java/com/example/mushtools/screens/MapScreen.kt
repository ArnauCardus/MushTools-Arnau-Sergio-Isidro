package com.example.mushtools.screens

import android.Manifest
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.example.mushtools.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        MapViewContainer(context, lifecycle, modifier = Modifier.fillMaxSize())
    } else {
        Text("La localización es necesaria para poder utilizar el mapa")
    }
}

@Composable
fun MapViewContainer(
    context: Context,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    // Load OpenStreetMap configuration
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences(
            context.getString(R.string.app_name),
            ComponentActivity.MODE_PRIVATE
        )
    )

    // Create MapView
    val mapView = MapView(context).apply {
        setMultiTouchControls(true)
    }

    // Bind MapView to the lifecycle
    mapView.controller.setZoom(6.0)

    // Add marker at current location
    addMarkerAtCurrentLocation(mapView, context)

    AndroidView(modifier = modifier, factory = { mapView })
}

private fun addMarkerAtCurrentLocation(mapView: MapView, context: Context) {
    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    myLocationOverlay.enableMyLocation()

    mapView.overlays.add(myLocationOverlay)

    myLocationOverlay.runOnFirstFix {
        val currentLocation = myLocationOverlay.myLocation
        if (currentLocation != null) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(currentLocation.latitude, currentLocation.longitude)
            marker.title = "Mi ubicación"
            mapView.overlays.add(marker)
        }
    }
}
