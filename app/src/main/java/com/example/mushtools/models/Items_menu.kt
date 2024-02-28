package com.example.mushtools.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mushtools.navegation.NavScreen

sealed class Items_menu(
    val icon: ImageVector,
    val title: String,
    val ruta: String
){
    object Item1 : Items_menu(
        Icons.Outlined.Info,
        "Aprender",
        NavScreen.AprenderScreen.name

    )
    object Item2 : Items_menu(
        Icons.Outlined.Album,
        "Mis Setas",
        NavScreen.MisSetasScreen.name

    )
    object Item3 : Items_menu(
        Icons.Outlined.AddAPhoto,
        "Fotos",
        NavScreen.FotosScreen.name

    )
    object Item4 : Items_menu(
        Icons.Outlined.AddLocation,
        "Mapa",
        NavScreen.MapScreen.name

    )
    object Item5 : Items_menu(
        Icons.Outlined.CheckCircle,
        "Quiz",
        NavScreen.QuizScreen.name

    )

    object Item6 : Items_menu(
        Icons.Outlined.Forum,
        "Forum",
        NavScreen.ForumScreen.name

    )
    object Item7 : Items_menu(
        Icons.Outlined.WbSunny,
        "Tiempo",
        NavScreen.TiempoScreen.name

    )
}
