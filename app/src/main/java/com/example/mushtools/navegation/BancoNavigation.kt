package com.example.mushtools.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.screens.AnadirFoto
import com.example.mushtools.screens.Aprender
import com.example.mushtools.screens.Forum
import com.example.mushtools.screens.Fotos
import com.example.mushtools.screens.Map
import com.example.mushtools.screens.MisSetas
import com.example.mushtools.screens.Quiz
import com.example.mushtools.screens.Scoreboard
import com.example.mushtools.screens.Tiempo


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BancoNavigation(
    navController: NavHostController
){
    var foto = ""
    var seta: Items_MisSetas = Items_MisSetas()
    var isediting: Boolean = false
    NavHost(
        navController = navController,
        startDestination = NavScreen.AprenderScreen.name
    ){
        composable(NavScreen.AprenderScreen.name){
            Aprender()
        }
        composable(NavScreen.FotosScreen.name){
            Fotos(
                onOk = {
                    foto = it
                    navController.navigate(route = NavScreen.AnadirFotoScreen.name)
                },
                onEditing = {
                    isediting=it
                }
            )
        }
        composable(NavScreen.MapScreen.name){
            Map()
        }
        composable(NavScreen.MisSetasScreen.name) {
            MisSetas(
                onEditSeta = {
                    seta = it
                    navController.navigate(route = NavScreen.AnadirFotoScreen.name)
                },
                isEditing = {
                    isediting = it
                }
            )
        }
        composable(NavScreen.QuizScreen.name){
            Quiz(navController)
        }
        composable(NavScreen.ForumScreen.name){
            Forum()
        }
        composable(NavScreen.ScoreboardScreen.name){
            Scoreboard()
        }
        composable(NavScreen.AnadirFotoScreen.name){
            AnadirFoto(navController, rutaImagen = foto, setaParaEditar = seta, isEditing = isediting)
        }
        composable(NavScreen.TiempoScreen.name){
            Tiempo()
        }
    }
}



