package com.example.mushtools.screens

import android.os.CountDownTimer
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
import androidx.compose.runtime.DisposableEffect
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
import com.example.mushtools.FireBase.GuardarScore
import com.example.mushtools.FireBase.ListarSetas
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.models.Items_Setas
import com.example.mushtools.navegation.NavScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@Composable
fun Quiz(navController: NavController) {
    var correctAnswersCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val selectedSeta = remember { mutableStateOf<Items_Setas?>(null) }
    val options = remember { mutableStateListOf<String>() }
    val selectedButtonStates = remember { mutableStateListOf<Pair<String, Color?>?>(null, null, null) }
    val setasLista = remember { mutableStateListOf<Items_Setas>() }

    LaunchedEffect(Unit) {
        ListarSetas { listSetas ->
            setasLista.addAll(listSetas)
            Log.d("QuizScreen", "Quiz: ${setasLista.toString()}")
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
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {


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
                                correctAnswersCount++
                            },
                            {
                                score = correctAnswersCount
                                GuardarScore(score)
                                correctAnswersCount = 0
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

                                selectedButtonStates.clear()
                            },
                            selectedButtonStates = selectedButtonStates
                        )
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
    onCorrectAnswer: () -> Unit,
    onIncorrectAnswer: () -> Unit,
    correctAnswersCount: Int,
    onNextQuestion: () -> Unit,
    selectedButtonStates: MutableList<Pair<String, Color?>?>
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var timeLeft by remember { mutableStateOf(10) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    // Obtener la URL de la imagen
    obtenerUrlDeImagen(seta.foto) { imageUrlFromFunction ->
        imageUrl = imageUrlFromFunction
    }
    DisposableEffect(Unit) {
        onDispose {
            timer?.cancel() // Cancelar el temporizador cuando se desecha el DisposableEffect
        }
    }
    // Reiniciar el temporizador cuando se cambia la pregunta
    LaunchedEffect(seta) {
        timer?.cancel() // Detener el temporizador anterior, si existe
        timeLeft = 10 // Reiniciar el tiempo restante

        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                // Si el tiempo se agota, proceder automáticamente
                onIncorrectAnswer()
                onNextQuestion() // Avanzar a la siguiente pregunta
            }
        }
        timer?.start() // Iniciar el temporizador para la nueva pregunta
    }

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
        Spacer(modifier = Modifier.height(16.dp))

        options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    if (option == seta.nombre) {
                        onCorrectAnswer()
                        // Asegurarse de que haya suficientes elementos en la lista
                        while (selectedButtonStates.size <= index) {
                            selectedButtonStates.add(null)
                        }
                        selectedButtonStates[index] = option to Color.Green
                    } else {
                        onIncorrectAnswer()
                        // Asegurarse de que haya suficientes elementos en la lista
                        while (selectedButtonStates.size <= index) {
                            selectedButtonStates.add(null)
                        }
                        selectedButtonStates[index] = option to Color.Red
                    }
                    MainScope().launch {
                        delay(1000) // Esperar 1 segundo
                        onNextQuestion() // Avanzar a la próxima pregunta después del retraso
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(
                        color = selectedButtonStates.getOrNull(index)?.second ?: Color.Transparent
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
        Text(
            text = "Tiempo restante: $timeLeft segundos",
        )
    }
}



