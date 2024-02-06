package com.example.mushtools.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Quiz(){
    Column (
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(text = "Quiz")
    }
}