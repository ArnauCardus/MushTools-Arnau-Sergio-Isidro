package com.example.mushtools.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mushtools.screens.AnadirFoto
import com.example.mushtools.screens.Aprender
import com.example.mushtools.screens.Fotos
import com.example.mushtools.screens.Map
import com.example.mushtools.screens.MisSetas
import com.example.mushtools.screens.Quiz

@Composable
fun BancoNavigation(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = NavScreen.AprenderScreen.name
    ){
        composable(NavScreen.AprenderScreen.name){
            Aprender()
        }
        composable(NavScreen.FotosScreen.name){
            Fotos(navController)
        }
        composable(NavScreen.MapScreen.name){
            Map()
        }
        composable(NavScreen.MisSetasScreen.name){
            MisSetas()
        }
        composable(NavScreen.QuizScreen.name){
            Quiz()
        }
        composable(NavScreen.AnadirFotoScreen.name){
            AnadirFoto(navController, ImageURL = "")
        }
    }
}


