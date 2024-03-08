package com.example.mushtools.screens
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtools.FireBase.eliminarSeta
import com.example.mushtools.FireBase.eliminarUsuariosCompartidos
import com.example.mushtools.FireBase.guardarPostCompartidos
import com.example.mushtools.FireBase.listarMisSetas
import com.example.mushtools.FireBase.obtenerListaUsuarios
import com.example.mushtools.FireBase.obtenerUrlDeImagen
import com.example.mushtools.FireBase.obtenerUsersShared
import com.example.mushtools.R
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.navegation.NavScreen

@Composable
fun MisSetas(
    onEditSeta: (Items_MisSetas) -> Unit,
    isEditing: (Boolean) -> Unit,
    navController: NavController
) {
    var setasList by remember { mutableStateOf<List<Items_MisSetas>>(emptyList()) }
    var usernames by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        obtenerListaUsuarios(
            onsuccess = { list ->
                usernames = list
            }
        )
        listarMisSetas(
            onok = { list ->
                val sortedList = list.sortedByDescending { it.fecha } // Ordenar por fecha descendente
                setasList = sortedList
            }
        )
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End){
            ElevatedButton(
                onClick = {navController.navigate(route = NavScreen.MisSetasScreen.name)},
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Mis Setas")
            }
            ElevatedButton(
                onClick = {navController.navigate(route = NavScreen.PostCompartidoScreen.name)},
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Compartidos")
            }

        }
        // Mostrar las setas en un LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(setasList) { seta ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 5.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    MisSetaItem(usernames, seta, onEditSeta, isEditing, navController)
                }
            }
        }
    }

}
@Composable
fun MisSetaItem(users: List<String>,seta: Items_MisSetas, onEditSeta: (Items_MisSetas) -> Unit,isEditing: (Boolean)-> Unit,navController: NavController) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedUsers by remember { mutableStateOf(emptyList<String>()) }
    var sharedUsers by remember { mutableStateOf(emptyList<String>()) }
    obtenerUsersShared(seta) { listaUsuarios ->
        sharedUsers = listaUsuarios
    }
    obtenerUrlDeImagen(seta.imagen,
        onSuccess = { imageUrlFromFunction ->
            imageUrl = imageUrlFromFunction
        }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Seta",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Comentario : " + seta.comentario)
        Text(text = "Fecha : ${seta.fecha}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ElevatedButton(
                onClick = {
                    isEditing(true)
                    onEditSeta(seta)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Editar")
            }

            ElevatedButton(
                onClick = {
                    eliminarSeta(seta)
                    navController.navigate(route = NavScreen.MisSetasScreen.name)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Eliminar")
            }
            ElevatedButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Share")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text("Buscar Usuario")
                    },
                    text = {
                        SearchableList(sharedUsers,users,
                            onUserSelected = { user ->
                                selectedUsers = if (selectedUsers.contains(user)) {
                                    selectedUsers - user
                                } else {
                                    selectedUsers + user
                                }
                            }
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    eliminarUsuariosCompartidos(selectedUsers,seta)
                                    showDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PersonRemove,
                                    contentDescription = "Eliminar"
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    guardarPostCompartidos(selectedUsers, seta)
                                    showDialog = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PersonAdd,
                                    contentDescription = "Agregar"
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Cerrar"
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}
@Composable
fun SearchableList(
    sharedusers: List<String>,
    searchableItems: List<String>,
    onUserSelected: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue()) }
    var filteredItems by remember { mutableStateOf(searchableItems) }
    var selectedItems by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                filteredItems = filterItems(it.text, searchableItems)
            },
            label = { Text("Buscar") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Perform action on keyboard done event */ }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Mostrar siempre la lista de "selectedItems"
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartido Con:")
            sharedusers.forEach { user ->
                Text(text = user)
            }
            Text("Usuarios seleccionados:")
            selectedItems.forEach { user ->
                Text(text = user)
            }
        }

        // Mostrar "filteredItems" solo si hay texto en el campo de búsqueda
        if (searchText.text.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredItems.forEach { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onUserSelected(item)
                                selectedItems = if (selectedItems.contains(item)) {
                                    selectedItems - item
                                } else {
                                    selectedItems + item
                                }
                            }
                    )
                }
            }
        }
    }
}


fun filterItems(query: String, items: List<String>): List<String> {
    return items.filter { it.contains(query, ignoreCase = true) }
}

