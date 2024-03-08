package com.example.mushtools.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.listarPostCompartidos
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.navegation.NavScreen

@Composable
fun PostCompartido(navController: NavController){
    var setasList by remember { mutableStateOf<List<Items_MisSetas>>(emptyList()) }
    listarPostCompartidos(
        onok = { list ->
            val sortedList = list.sortedByDescending { it.fecha } // Ordenar por fecha descendente
            setasList = sortedList
        }
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End){
            ElevatedButton(
                onClick = {navController.navigate(route = NavScreen.MisSetasScreen.name)},
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Mis Setas")
            }
            ElevatedButton(
                onClick = {navController.navigate(route = NavScreen.PostCompartidoScreen.name)},
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Compartidos")
            }

        }
        // Mostrar las setas en un LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(setasList) { seta ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 5.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    MisSetaItem(seta)
                }
            }
        }
    }
}
@Composable
fun MisSetaItem(seta: Items_MisSetas) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    obtenerUrlDeImagen(seta.imagen,
        onSuccess = { imageUrlFromFunction ->
            imageUrl = imageUrlFromFunction
        }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Seta",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Creado por :"+ seta.usuario)
        Text(text = "Comentario : " + seta.comentario)
        Text(text = "Fecha : ${seta.fecha}")
    }
}