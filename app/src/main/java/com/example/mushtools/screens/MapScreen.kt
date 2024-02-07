package com.example.mushtools.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mushtools.funciones.funciones_maps.MapsScreen

@Composable
fun Map() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        MapsScreen()
    }
}