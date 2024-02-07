package com.example.mushtools.funciones


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mushtools.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : ComponentActivity(), MapListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsScreen()
        }
    }

    @Composable
    fun MapsScreen() {
        val context = LocalContext.current
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        )

        val mapView = rememberMapViewWithLifecycle()

        LaunchedEffect(mapView) {
            val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            val controller = mapView.controller
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.enableFollowLocation()
            myLocationOverlay.isDrawAccuracyEnabled = true
            myLocationOverlay.runOnFirstFix {
                controller.setCenter(myLocationOverlay.myLocation)
                controller.animateTo(myLocationOverlay.myLocation)
            }
            controller.setZoom(6.0)
            mapView.overlays.add(myLocationOverlay)
            mapView.addMapListener(this@MainActivity)
        }

        MapViewContainer(mapView)
    }

    @Composable
    fun MapViewContainer(mapView: MapView) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        event?.source?.let {
            val latitude = it.mapCenter.latitude
            val longitude = it.mapCenter.longitude
            Log.e("TAG", "onScroll: Latitude: $latitude, Longitude: $longitude")
        }
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom: Zoom level: ${event?.zoomLevel}, Source: ${event?.source}")
        return false
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        val mapView = MapView(context)
        mapView
    }

    return mapView
}
