package com.example.mushtools.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mushtools.FireBase.ListarScore
import com.example.mushtools.R
import com.example.mushtools.models.Scoreboard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Scoreboard() {
    var ScoreLista by remember { mutableStateOf<List<Scoreboard>>(emptyList()) }
    ListarScore(
        onok = { list ->
            ScoreLista = list.sortedByDescending { it.score }
        }
    )

    val fechaActual = LocalDate.now()
    println("Fecha actual: $fechaActual") // Registrar la fecha actual

    var filterType by remember { mutableStateOf(FilterType.NONE) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            //Spacer(modifier = Modifier.width(36.dp))
            FilterButton(text = "Por Día", onClick = { filterType = FilterType.DAY })

           // Spacer(modifier = Modifier.width(16.dp))
            FilterButton(text = "Por Mes", onClick = { filterType = FilterType.MONTH })

           // Spacer(modifier = Modifier.width(16.dp))
            FilterButton(text = " Por Año ", onClick = { filterType = FilterType.YEAR })

        }
        Text(
            text = "Lista de Puntuaciones ",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ScoreboardList(scores = filterScores(ScoreLista, filterType))
    }
}

@Composable
fun FilterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(48.dp) // Ajusta la altura del botón
            .width(130.dp) // Ajusta el ancho del botón
    ) {
        Text(text = text)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun filterScores(scores: List<Scoreboard>, filterType: FilterType): List<Scoreboard> {
    val fechaActual = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Define el formato del texto de la fecha
    return when (filterType) {
        FilterType.DAY -> scores.filter { LocalDate.parse(it.fecha, dateFormatter).run { dayOfYear == fechaActual.dayOfYear && monthValue == fechaActual.monthValue && year == fechaActual.year } }
        FilterType.MONTH -> scores.filter { LocalDate.parse(it.fecha, dateFormatter).run { monthValue == fechaActual.monthValue && year == fechaActual.year } }
        FilterType.YEAR -> scores.filter { LocalDate.parse(it.fecha, dateFormatter).year == fechaActual.year }
        else -> scores
    }
}

enum class FilterType {
    NONE,
    DAY,
    MONTH,
    YEAR
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScoreboardList(scores: List<Scoreboard>) {
    LazyColumn {
        itemsIndexed(scores) { index, score ->
            ScoreCard(score = score, position = index + 1)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ScoreCard(score: Scoreboard, position: Int) {
    val borderColor = when (position) {
        1 -> Color(0xFFC4EE1D) // Dorado para la primera posición
        2 -> Color(0xFF093C9B) // Azul para la segunda posición
        3 -> Color(0xFFA30E0C) // Rojo para la tercera posición
        else -> Color(0xFF6D6666) // Sin borde para el resto de las posiciones
    }

    val backgroundColor = when (position) {
        1 -> Color(0xFFDAA520) // Dorado para la primera posición
        2 -> Color(0xFF4169E1) // Azul para la segunda posición
        3 -> Color(0xFFDC143C) // Rojo para la tercera posición
        else -> Color.Gray // Gris para el resto de las posiciones
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(5.dp, borderColor) // Añadir un borde con el color determinado
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = backgroundColor)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "$position.", // Mostrar la posición
                    color = Color.White,
                    textAlign = TextAlign.Start, // Alinear el texto al inicio
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp) // Ajustar el relleno
                )
                Text(
                    text = "${score.userId}",
                    color = Color.White,
                    modifier = Modifier.weight(1f) // Ocupa el máximo espacio disponible
                )
                val image: Painter = painterResource(id = R.drawable.icon) // Reemplaza "ic_seta" con el nombre real de tu imagen de seta
                Image(
                    painter = image,
                    contentDescription = "Seta",
                    modifier = Modifier.size(35.dp).padding(end = 8.dp) // Ajustar el relleno a la derecha
                )
                Text(
                    text = "${score.score}",
                    color = Color.White,
                    textAlign = TextAlign.End, // Alinear el texto al final
                    modifier = Modifier.padding(end = 16.dp) // Ajustar el relleno a la derecha
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
