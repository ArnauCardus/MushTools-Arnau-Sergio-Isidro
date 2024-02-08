package com.example.mushtools.screens


import android.content.ContentValues
import android.util.Log
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
import com.example.mushtools.models.Items_Setas
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Quiz() {
    val db = FirebaseFirestore.getInstance()
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        val setasLista = remember { mutableStateListOf<Items_Setas>() }

        db.collection("Setas").get().addOnSuccessListener { result ->
            for (document in result) {
                val seta: Items_Setas = document.toObject(Items_Setas::class.java)
                setasLista.add(seta)
                Log.d("Setas", "$seta")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }

        val selectedSeta = remember { mutableStateOf<Items_Setas?>(null) }

        val options = remember { mutableStateListOf<String>() }

        if (setasLista.isNotEmpty() && selectedSeta.value == null) {
            selectedSeta.value = setasLista.random() // Selecciona una seta aleatoria
            val correctAnswer = selectedSeta.value!!.nombre // Respuesta correcta

            options.clear() // Limpiar las opciones
            options.add(correctAnswer) // Agrega la respuesta correcta

            while (options.size < 3) {
                val randomSeta = setasLista.random().nombre // Obtén una seta aleatoria

                // Asegúrate de que la opción aleatoria no sea la respuesta correcta y no esté ya en la lista de opciones
                if (randomSeta != correctAnswer && randomSeta !in options) {
                    options.add(randomSeta) // Agrega la opción incorrecta única
                }
            }

            options.shuffle() // Mezcla las opciones
        }


        // Mostrar la foto de la seta y las opciones en un LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
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
                ) {
                    selectedSeta.value?.let { SetaQuizItem(it, options) }
                }
            }
        }
    }
}

@Composable
fun SetaQuizItem(seta: Items_Setas, options: List<String>) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "¿Qué seta es esta?",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AsyncImage(
            model = seta.foto,
            contentDescription = seta.nombre,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp)) // Añadir espacio entre la imagen y las opciones
        for (option in options) {
            Text(
                text = option,

                modifier = Modifier
                    .clickable {
                        selectedAnswer = option
                    }
                    .padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedAnswer != null) {
            if (selectedAnswer == seta.nombre) {
                Text(text = "¡Respuesta correcta!", color = Color.Green)
            } else {
                Text(text = "Respuesta incorrecta. La respuesta correcta es: ${seta.nombre}", color = Color.Red)
            }
        }
    }
}
