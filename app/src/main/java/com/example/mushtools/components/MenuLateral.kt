package com.example.mushtools.components

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mushtools.models.Items_menu.Item1
import com.example.mushtools.models.Items_menu.Item2
import com.example.mushtools.models.Items_menu.Item3
import com.example.mushtools.models.Items_menu.Item4
import com.example.mushtools.models.Items_menu.Item5
import com.example.mushtools.models.Items_menu.Item6
import com.example.mushtools.models.Items_menu.Item7
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
        Item6,
        Item7,
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
        },
        gesturesEnabled = currentRoute(navController) !in listOf(Item4.ruta)
    ) {
        contenido()
    }
}
