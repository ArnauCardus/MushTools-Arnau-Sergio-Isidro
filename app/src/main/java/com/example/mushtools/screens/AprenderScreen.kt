package com.example.mushtools.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.ListarSetas
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.models.Items_Setas

@Composable
fun Aprender() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val Setaslista = remember { mutableStateListOf<Items_Setas>() }
        ListarSetas(
            onok = { listSetas ->
                Setaslista.clear()
                Setaslista.addAll(listSetas)
            }
        )

        // Mostrar las setas en un LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(Setaslista) { seta ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 5.dp)
                        .background(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                { SetaItem(seta)}

                // Aquí puedes mostrar otros atributos de la seta
            }
        }
    }
}
@Composable
fun SetaItem(seta: Items_Setas) {
    var descripcionVisible by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    obtenerUrlDeImagen(seta.foto,
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
            contentDescription = seta.nombre,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
                .clickable { descripcionVisible = !descripcionVisible }
        )
        Spacer(modifier = Modifier.height(16.dp))


        if (descripcionVisible) {
            Text(text = "Nombre : " + seta.nombre)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nombre Cientifico : " +seta.nombrecient)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tipo : "+seta.tipo)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = seta.descripcion)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
