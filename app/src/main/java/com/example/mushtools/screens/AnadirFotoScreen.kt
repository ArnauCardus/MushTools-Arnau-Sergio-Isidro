package com.example.mushtools.screens

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
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import android.Manifest
import androidx.compose.ui.platform.LocalContext
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AnadirFoto(navController: NavController, rutaImagen : String){
    var contador by remember { mutableStateOf(1) }
    fun generarNombreSeta(): String {
        val nombre = "seta$contador"
        contador++
        return nombre
    }
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        var currentLatitude by remember { mutableStateOf<Double?>(null) }
        var currentLongitude by remember { mutableStateOf<Double?>(null) }
    } else {
        Text("La localización es necesaria para poder utilizar el mapa")
    }
    var seta by remember { mutableStateOf(Items_MisSetas(rutaImagen,"","",""))}
    var comentario by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    obtenerUrlDeImagen(seta.imagen,
        onSuccess = { imageUrlFromFunction ->
            imageUrl = imageUrlFromFunction
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = imageUrl ?: "Imagen no disponible",
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
        OutlinedTextField (
            value = comentario,
            onValueChange = { comentario = it
                seta = seta.copy(comentario = it)
                            },
            label = {
                Text("Comentario")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )
        ElevatedButton(
            onClick = { agregarSeta(seta)
                navController.navigate(route = NavScreen.MisSetasScreen.name)
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar ")
        }
    }

}
fun agregarSeta(seta : Items_MisSetas) {
    val db = FirebaseFirestore.getInstance()
    db.collection("MisSetas")
        .add(seta)
        .addOnSuccessListener { documentReference ->
            println("Usuario agregado con ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error al agregar usuario: $e")
        }
}

