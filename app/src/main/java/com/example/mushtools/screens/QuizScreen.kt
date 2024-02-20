package com.example.mushtools.screens

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.models.Items_Setas
import com.example.mushtools.models.Scoreboard
import com.example.mushtools.navegation.NavScreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Quiz(navController: NavController) {
    // Aquí se mantiene un mapa de preguntas y sus colores de botones seleccionados
    val selectedAnswersState = remember { mutableMapOf<Items_Setas, Pair<String?, Color?>>() }
    val db = FirebaseFirestore.getInstance()
    var correctAnswersCount by remember { mutableStateOf(0) }
    var lastSelectedSeta by remember { mutableStateOf<Items_Setas?>(null) }
    var score by remember { mutableStateOf(Scoreboard(0,""))}


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val setasLista by remember { mutableStateOf(mutableListOf<Items_Setas>()) }
        val selectedSeta = remember { mutableStateOf<Items_Setas?>(null) }
        val options = remember { mutableStateListOf<String>() }

        LaunchedEffect(Unit) {
            db.collection("Setas").get().addOnSuccessListener { result ->
                for (document in result) {
                    val seta: Items_Setas = document.toObject(Items_Setas::class.java)
                    setasLista.add(seta)
                    Log.d("Setas", "$seta")
                }
                selectedSeta.value = setasLista.random()
                val correctAnswer = selectedSeta.value!!.nombre

                options.clear()
                options.add(correctAnswer)

                while (options.size < 3) {
                    val randomSeta = setasLista.random().nombre

                    if (randomSeta != correctAnswer && randomSeta !in options) {
                        options.add(randomSeta)
                    }
                }

                options.shuffle()
            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
        }

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
                    selectedSeta.value?.let { seta ->
                        SetaQuizItem(
                            seta,
                            options,
                            {
                                selectedSeta.value = null // Reinicia  seta
                                correctAnswersCount = 0 // Reinicia respuestas correctas
                                selectedAnswersState.clear() // Reiniciar el estado de los colores de los botones seleccionados
                            },
                            {
                                correctAnswersCount++
                            },
                            {
                                score=score.copy(score=correctAnswersCount)
                                GuardarScore(score)
                                correctAnswersCount = 0 // Reiniciar Respuestas Correctas a 0
                            },
                            correctAnswersCount,
                            onNextQuestion = {

                                selectedSeta.value = setasLista.random()
                                val correctAnswer = selectedSeta.value!!.nombre

                                options.clear()
                                options.add(correctAnswer)

                                while (options.size < 3) {
                                    val randomSeta = setasLista.random().nombre

                                    if (randomSeta != correctAnswer && randomSeta !in options) {
                                        options.add(randomSeta)
                                    }
                                }

                                options.shuffle()

                                // Reiniciar el estado del botón seleccionado para la próxima pregunta
                                selectedAnswersState[selectedSeta.value!!] = null to null
                            },
                            // Pasar el estado del botón seleccionado para la pregunta actual
                            selectedButtonState = selectedAnswersState[seta]
                        ) { text, color ->
                            // Actualizar el estado del botón seleccionado
                            selectedAnswersState[seta] = text to color

                        }

                    }
                }
                FloatingActionButton(onClick = { navController.navigate(route = NavScreen.ScoreboardScreen.name) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Scoreboard, "Scoreboard" )
                        Text(text = " Scoreboard")
                    }
                }
            }
        }
    }
}
@Composable
fun SetaQuizItem(
    seta: Items_Setas,
    options: List<String>,
    onPlayAgain: () -> Unit,
    onCorrectAnswer: () -> Unit,
    onIncorrectAnswer: () -> Unit,
    correctAnswersCount: Int,
    onNextQuestion: () -> Unit,
    selectedButtonState: Pair<String?, Color?>?, // Estado del botón seleccionado para la pregunta actual
    onButtonStateSelected: (String, Color) -> Unit // Callback para actualizar el estado del botón seleccionado

) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var selectedButtonColor by remember { mutableStateOf<Color?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    obtenerUrlDeImagen(seta.foto,
        onSuccess = { imageUrlFromFunction ->
            imageUrl = imageUrlFromFunction
        })
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
            model = imageUrl,
            contentDescription = seta.nombre,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp)) // Añadir espacio entre la imagen y las opciones
        for (option in options) {
            Button(
                onClick = {
                    if (option == seta.nombre) {
                        onCorrectAnswer()
                        selectedButtonColor = Color.Green
                    } else {
                        onIncorrectAnswer()
                        selectedButtonColor = Color.Red
                    }
                    selectedOption = option // Actualizar la opción seleccionada
                    onButtonStateSelected(option, selectedButtonColor!!)
                    MainScope().launch {
                        delay(1000) // Esperar 1 segundo
                        selectedButtonColor = null // Restablecer el color del botón
                        onNextQuestion() // Avanzar a la próxima pregunta después del retraso
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(
                        color = when {
                            option == selectedButtonState?.first || option == selectedOption -> selectedButtonColor
                                ?: Color.Transparent

                            else -> Color.Transparent
                        }
                    )

            ) {
                Text(text = option)

            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Respuestas correctas: $correctAnswersCount",
        )

    }
}


fun GuardarScore (score:Scoreboard){
    val db = FirebaseFirestore.getInstance()
    db.collection("Scoreboard")
        .add(score)

}