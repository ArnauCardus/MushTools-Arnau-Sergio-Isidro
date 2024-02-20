package com.example.mushtools.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.mushtools.models.Scoreboard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Scoreboard() {
    val db = FirebaseFirestore.getInstance()
    val scoreList = remember { mutableStateListOf<Scoreboard>() }

    LaunchedEffect(true) {
        db.collection("Scoreboard").get().addOnSuccessListener { result ->
            for (document in result) {
                val score: Scoreboard = document.toObject(Scoreboard::class.java)
                scoreList.add(score)
            }
            scoreList.sortByDescending { it.score } // Ordenar la lista por puntaje de mayor a menor
        }
    }

    Column {
        Text(text = "Lista de Puntuaciones:")
        ScoreboardList(scores = scoreList)
    }
}

@Composable
fun ScoreboardList(scores: List<Scoreboard>) {
    LazyColumn {
        items(scores) { score ->
            Text(text = "Puntuación: ${score.score}")
            // Aquí puedes mostrar más detalles del puntaje si lo deseas
        }
    }
}
