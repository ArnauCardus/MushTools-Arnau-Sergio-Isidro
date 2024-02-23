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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Scoreboard() {
    val db = FirebaseFirestore.getInstance()
    val scoreList = remember { mutableStateListOf<Scoreboard>() }

    val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtenemos el ID del usuario actual

    LaunchedEffect(true) {
        db.collection("Scoreboard").get().addOnSuccessListener { result ->
            for (document in result) {
                val score: Scoreboard = document.toObject(Scoreboard::class.java)
                // Asignamos el ID del usuario al puntaje
                score.userId = document.id
                scoreList.add(score)
                // Aquí guardamos el ID del usuario en Firestore
                db.collection("Scoreboard").document(document.id).update("userId", userId)
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
            Text(text = "Usuario: ${score.userId}, Puntuación: ${score.score}")
            // Aquí puedes mostrar más detalles del puntaje si lo deseas
        }
    }
}
