package com.example.mushtools.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.mushtools.FireBase.RestaurantesRepository
import com.example.mushtools.FireBase.listarMisSetas
import com.example.mushtools.FireBase.listarPostCompartidos
import com.example.mushtools.R
import com.example.mushtools.models.Restaurantes
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

    val mapView = MapView(context).apply {
        setMultiTouchControls(true)
    }

    mapView.controller.setZoom(6.0)

    LaunchedEffect(Unit) {
        addMarkerAtCurrentLocation(mapView, context)
        addMarkersForUserSetas(mapView, context)
        marcadorPostCompartidos(mapView,context)
        val restaurantesRepository = RestaurantesRepository()
        val restaurantesList = restaurantesRepository.getRestaurantes()
        addMarkersForRestaurantes(mapView, context, restaurantesList)
    }

    AndroidView(modifier = modifier, factory = { mapView })
}

// Resto del código igual que lo tienes


var currentLatitude: Double? = null
var currentLongitude: Double? = null
private fun addMarkerAtCurrentLocation(mapView: MapView, context: Context) {
    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    myLocationOverlay.enableMyLocation()

    mapView.overlays.add(myLocationOverlay)

    myLocationOverlay.runOnFirstFix {
        val currentLocation = myLocationOverlay.myLocation
        if (currentLocation != null) {
            currentLatitude = currentLocation.latitude
            currentLongitude = currentLocation.longitude
            val marker = Marker(mapView)
            marker.position = GeoPoint(currentLocation.latitude, currentLocation.longitude)
            marker.title = "Mi ubicación"
            mapView.overlays.add(marker)
        }
    }
}

private  fun addMarkersForUserSetas(mapView: MapView, context: Context) {
    // Obtener la lista de setas de manera asincrónica
    listarMisSetas { misSetasList ->
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)

        // Crear un ícono rojo para las setas y escalarlo
        val setaDrawable = ContextCompat.getDrawable(context, R.drawable.icono)
        if (setaDrawable != null) {
            val setaIcon = BitmapDrawable(context.resources, Bitmap.createScaledBitmap((setaDrawable as BitmapDrawable).bitmap, 50, 50, true))
            setaIcon.setBounds(0, 0, setaIcon.intrinsicWidth, setaIcon.intrinsicHeight)

            // Add markers for each mushroom associated with the user
            misSetasList.forEach { seta ->
                val latitude = seta.latitude
                val longitude = seta.longitude
                if (latitude != null && longitude != null) {
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(latitude, longitude)
                    marker.title = seta.comentario
                    marker.icon = setaIcon
                    mapView.overlays.add(marker)
                }
            }
        }
    }
}
private  fun marcadorPostCompartidos(mapView: MapView, context: Context) {
    // Obtener la lista de setas de manera asincrónica
    listarPostCompartidos { misSetasList ->
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)

        // Crear un ícono rojo para las setas y escalarlo
        val setaDrawable = ContextCompat.getDrawable(context, R.drawable.compartidos)
        if (setaDrawable != null) {
            val setaCompartidos = BitmapDrawable(context.resources, Bitmap.createScaledBitmap((setaDrawable as BitmapDrawable).bitmap, 50, 50, true))
            setaCompartidos.setBounds(0, 0, setaCompartidos.intrinsicWidth, setaCompartidos.intrinsicHeight)

            // Add markers for each mushroom associated with the user
            misSetasList.forEach { seta ->
                val latitude = seta.latitude
                val longitude = seta.longitude
                if (latitude != null && longitude != null) {
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(latitude, longitude)
                    marker.title = seta.comentario
                    marker.icon = setaCompartidos
                    mapView.overlays.add(marker)
                }
            }
        }
    }
}

private suspend fun addMarkersForRestaurantes(
    mapView: MapView,
    context: Context,
    restaurantesList: List<Restaurantes>
) {

    val restaurantesList = RestaurantesRepository().getRestaurantes()

    val restauranteDrawable = ContextCompat.getDrawable(context, R.drawable.restaurante_icono)
    if (restauranteDrawable != null) {
        val restauranteIcon = BitmapDrawable(context.resources, Bitmap.createScaledBitmap((restauranteDrawable as BitmapDrawable).bitmap, 50, 50, true))
        restauranteIcon.setBounds(0, 0, restauranteIcon.intrinsicWidth, restauranteIcon.intrinsicHeight)


        restaurantesList.forEach { restaurante ->
            val latitude = restaurante.latitude.toDoubleOrNull()
            val longitude = restaurante.longitude.toDoubleOrNull()
            if (latitude != null && longitude != null) {
                val marker = Marker(mapView)
                marker.position = GeoPoint(latitude, longitude)
                marker.title = restaurante.nombre
                marker.icon = restauranteIcon
                mapView.overlays.add(marker)
            }
        }
    }
}




