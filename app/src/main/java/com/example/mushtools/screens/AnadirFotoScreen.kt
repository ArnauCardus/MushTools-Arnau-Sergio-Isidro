package com.example.mushtools.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.GuardarMisSetas
import com.example.mushtools.FireBase.editarSeta
import com.example.mushtools.FireBase.eliminarFotoStorage
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AnadirFoto(navController: NavController,  rutaImagen: String, setaParaEditar: Items_MisSetas, isEditing: Boolean) {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (isEditing != false) {
        EditarSeta(navController, setaParaEditar)
    } else {
        CrearSeta(navController, rutaImagen, permissionState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CrearSeta(navController: NavController, rutaImagen: String, permissionState: PermissionState) {
    var seGuardoSeta by remember { mutableStateOf(false) } // Variable para controlar si se guardó la seta

    Log.d("Crear Seta", "CrearSeta:$rutaImagen)")
    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }
    val context = LocalContext.current
    if (permissionState.status.isGranted) {
        Log.d("Merequetenge", "AnadirFoto: Fuera de location ${obtenerUbicacion(context)}")
        obtenerUbicacion(context)?.let { loc ->
            currentLatitude = loc.latitude
            currentLongitude = loc.longitude
            Log.d("Merequetenge", "AnadirFoto: Dentro de location - Latitud: $currentLatitude, Longitud: $currentLongitude")
        }
    }
    val dateTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    var seta by remember { mutableStateOf(Items_MisSetas(rutaImagen, "", dateTime,currentLatitude,currentLongitude, "")) }
    var comentario by remember { mutableStateOf("") }
    var imagenUrl: String? by remember { mutableStateOf("") }
    obtenerUrlDeImagen(rutaImagen) { imageUrlFromFunction ->
        imagenUrl = imageUrlFromFunction
    }

    DisposableEffect(true) {
        onDispose {

            if (!seGuardoSeta) {
                eliminarFotoStorage(seta,
                    onSuccess = {

                        println("Foto eliminada exitosamente de Firebase Storage.")
                    },
                    onError = { exception ->

                        println("Error al eliminar la foto: ${exception.message}")
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = imagenUrl,
            contentDescription = imagenUrl ?: "Imagen no disponible",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        // Botón Elevated
        ElevatedButton(
            onClick = { navController.navigate(route = NavScreen.FotosScreen.name) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hacer Foto + ")
        }

        OutlinedTextField(
            value = comentario,
            onValueChange = {
                comentario = it
                seta = seta.copy(comentario = it)
            },
            label = { Text("Comentario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )
        ElevatedButton(
            onClick = {
                Log.d("Info", "AnadirFoto: ${seta.toString()}")
                GuardarMisSetas(seta)
                seGuardoSeta = true
                navController.navigate(route = NavScreen.MisSetasScreen.name)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}


@Composable
fun EditarSeta(navController: NavController,setaParaEditar: Items_MisSetas) {
    var seta by remember { mutableStateOf(setaParaEditar) }
    Log.d("Editar Seta", "EditarSeta:$setaParaEditar.toString()")
    var comentario by remember { mutableStateOf(setaParaEditar.comentario) }
    var imagenUrl: String? by remember { mutableStateOf("") }
    obtenerUrlDeImagen(setaParaEditar.imagen) { imageUrlFromFunction ->
        imagenUrl = imageUrlFromFunction
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = imagenUrl,
            contentDescription = imagenUrl ?: "Imagen no disponible",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = comentario,
            onValueChange = {
                comentario = it
                seta = seta.copy(comentario = it)
            },
            label = { Text("Editar Comentario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )
        ElevatedButton(
            onClick = {
                Log.d("Info", "AnadirFoto: ${seta.toString()}")
                editarSeta(seta)
                navController.navigate(route = NavScreen.MisSetasScreen.name)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edita")
        }
    }
}


fun obtenerUbicacion(context: android.content.Context): android.location.Location? {
    val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager?
    if (locationManager != null &&
        context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
        context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        return locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
    }
    return null
}
