package com.example.mushtools.components

import TopBar
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mushtools.navegation.BancoNavigation


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    MenuLateral(navController = navController, drawerState = drawerState) {
        Contenido(navController = navController, drawerState = drawerState)
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Contenido(
    navController: NavHostController,
    drawerState: DrawerState
) {
    Scaffold(
        topBar = {
            TopBar(drawerState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            BancoNavigation(navController = navController)
        }

    }
}