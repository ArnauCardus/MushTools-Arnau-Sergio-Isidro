package com.example.mushtools.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnadirFoto(navController: NavController, ImageURL : String){
    var seta by remember { mutableStateOf(Items_MisSetas(ImageURL,"",0))}
    var comentario by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
            onClick = { agregarUsuario(seta)
                navController.navigate(route = NavScreen.MisSetasScreen.name)
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar ")
        }
    }

}
fun agregarUsuario(seta : Items_MisSetas) {
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


