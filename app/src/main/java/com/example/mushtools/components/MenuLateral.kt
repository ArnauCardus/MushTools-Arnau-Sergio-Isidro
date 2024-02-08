package com.example.mushtools.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mushtools.models.Items_menu.*
import com.example.mushtools.navegation.currentRoute
import kotlinx.coroutines.launch

@Composable
fun MenuLateral(
    navController: NavHostController,
    drawerState: DrawerState,
    contenido: @Composable () -> Unit
){
    val scope = rememberCoroutineScope()
    val Items_menu = listOf(
        Item1,
        Item2,
        Item3,
        Item4,
        Item5,
    )
    ModalNavigationDrawer(


        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {
                Text("Menu Setas", modifier = Modifier.padding(32.dp))
                Divider()
                Items_menu.forEach{item->
                    NavigationDrawerItem(
                        icon = {
                               Icon(item.icon,null)
                        },
                        label = { Text(text = item.title) },
                        selected = currentRoute(navController) == item.ruta,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navController.navigate(item.ruta)
                        }

                    )

                }
            }
        }) {
        contenido()
    }
}
