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
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun AnadirFoto(navController: NavController){
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
            onValueChange = { comentario = it },
            label = {
                Text("Comentario")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )
        ElevatedButton(
            onClick = { navController.navigate(route = NavScreen.FotosScreen.name) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar ")
        }
    }
}

