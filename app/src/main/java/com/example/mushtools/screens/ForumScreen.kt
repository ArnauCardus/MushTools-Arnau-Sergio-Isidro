package com.example.mushtools.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mushtools.models.Publicaciones
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Forum() {
    val publicacionList = remember { mutableStateListOf<Publicaciones>() }
    val showDialog = remember { mutableStateOf(false) }
    val titulo = remember { mutableStateOf("") }
    val contenido = remember { mutableStateOf("") }

    // Obtener publicaciones desde Firebase
    LaViewModel().getPublicaciones(publicacionList)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Foro") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Ajustar el padding aquí si es necesario
            ) {
                // Nuevo botón para agregar una publicación
                Spacer(modifier = Modifier.height(32.dp)) // Espacio entre el botón y la lista
                AddPublicacionButton(titulo.value, contenido.value) { showDialog.value = true }

                ForumContent(publicacionList)
            }
        }
    )

    if (showDialog.value) {
        AgregarPublicacionDialog(
            onDismiss = { showDialog.value = false },
            onConfirm = {
                val nuevaPublicacion = Publicaciones(
                    id = "", // Dejar que Firebase genere un ID automáticamente
                    titulo = titulo.value,
                    contenido = contenido.value,
                    comentarios = emptyList()
                )
                LaViewModel().addPublicacion(nuevaPublicacion)
                showDialog.value = false
            },
            titulo = titulo,
            contenido = contenido
        )
    }
}

@Composable
fun AddPublicacionButton(titulo: String, contenido: String, function: () -> Unit) {
    Button(
        onClick = function,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Agregar Publicación")
    }
}


@Composable
fun AgregarPublicacionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titulo: MutableState<String>,
    contenido: MutableState<String>
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = titulo.value,
                    onValueChange = { titulo.value = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = contenido.value,
                    onValueChange = { contenido.value = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}



@Composable
fun ForumContent(publicacionesList: List<Publicaciones>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp) // Padding horizontal para la lista
    ) {
        items(publicacionesList) { publicacion ->
            PublicacionItem(publicacion)
        }
    }
}

@Composable
fun PublicacionItem(publicacion: Publicaciones) {
    var newComment by remember { mutableStateOf("") } // Nuevo comentario ingresado por el usuario
    val comentarios = remember { mutableStateListOf(*publicacion.comentarios.toTypedArray()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.inversePrimary,
                shape = RoundedCornerShape(10.dp)
            )

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = publicacion.titulo,
                style = MaterialTheme.typography.titleLarge // Estilo h1 de Material Design
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = publicacion.contenido)

            // Agregar un Divider después del contenido de la publicación
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Título "Comentarios"
            Text(
                text = "Comentarios",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mostrar los comentarios
            comentarios.forEach { comentario ->
                CommentItem(comentario)
            }

            // Campo de texto para ingresar nuevo comentario
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Nuevo Comentario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Botón para agregar el nuevo comentario
            Button(
                onClick = {
                    if (newComment.isNotEmpty()) {
                        // Agregar el nuevo comentario a la lista de comentarios de la publicación
                        comentarios.add(newComment)
                        // Actualizar Firebase con el nuevo comentario
                        updateFirebase(publicacion.id, comentarios)
                        // Limpiar el campo de texto después de agregar el comentario
                        newComment = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Agregar Comentario")
            }
        }
    }
}


@Composable
fun CommentItem(comment: String) {
    Card(
        modifier = Modifier

            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)

        ) {
            Text(text = comment)
        }
    }
}

class LaViewModel {
    fun getPublicaciones(publicacionesList: MutableList<Publicaciones>) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Forum").get()
            .addOnSuccessListener { result ->
                // Limpiar la lista antes de agregar las nuevas publicaciones
                publicacionesList.clear()
                for (document in result) {
                    val publicacion = document.toObject(Publicaciones::class.java)
                    publicacionesList.add(publicacion)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
            }
    }

    fun addPublicacion(publicacion: Publicaciones) {
        val db = FirebaseFirestore.getInstance()
        val nuevaPublicacion = publicacion.copy(id = UUID.randomUUID().toString()) // Generar un ID aleatorio
        db.collection("Forum").add(nuevaPublicacion)
            .addOnSuccessListener { documentReference ->
                // Manejar éxito
            }
            .addOnFailureListener { exception ->
                // Manejar error
            }
    }

}

@Composable
fun AddPublicacionButton(function: () -> Unit) {
    Button(
        onClick = {
            // Crear una nueva publicación y agregarla a Firebase
            val nuevaPublicacion = Publicaciones(

                comentarios = emptyList()
            )
            LaViewModel().addPublicacion(nuevaPublicacion)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Agregar Publicación")
    }
}



fun updateFirebase(publicacionId: String, comentarios: List<String>) {
    val db = FirebaseFirestore.getInstance()
    val collectionReference = db.collection("Forum")

    // Consultar el documento correcto utilizando el campo id
    collectionReference.whereEqualTo("id", publicacionId).get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                // Actualizar los comentarios en Firebase
                document.reference.update("comentarios", comentarios)
            }
        }
        .addOnFailureListener { exception ->
            // Manejar el error de la consulta
        }
}
