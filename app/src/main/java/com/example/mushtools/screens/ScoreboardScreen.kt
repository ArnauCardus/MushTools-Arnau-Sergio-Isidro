package com.example.mushtools.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mushtools.FireBase.ListarScore
import com.example.mushtools.models.Scoreboard

@Composable
fun Scoreboard() {
    var ScoreLista by remember { mutableStateOf<List<Scoreboard>>(emptyList()) }

    ListarScore(
        onok = { listSetas ->
            ScoreLista = listSetas
        }
    )

    Column {
        Text(text = "Lista de Puntuaciones:")
        ScoreboardList(scores = ScoreLista)
    }
}

@Composable
fun ScoreboardList(scores: List<Scoreboard>) {
    LazyColumn {
        items(scores) { score ->
            Text(text = "Username: ${score.userId}, Score: ${score.score}")
            // Aquí puedes mostrar más detalles del puntaje si lo deseas
        }
    }
}
