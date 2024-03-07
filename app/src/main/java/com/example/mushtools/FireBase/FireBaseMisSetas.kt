package com.example.mushtools.FireBase

import android.util.Log
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.models.PostCompartidos
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlin.math.log

fun GuardarMisSetas(seta: Items_MisSetas){

    val db = FirebaseFirestore.getInstance()
    var nombreUsuario: String
    obtenerUsuario(
        onsuccess = { nombre ->
            nombreUsuario = nombre
            var setamutable = seta.copy(usuario = nombreUsuario)
            db.collection("MisSetas")
                .add(setamutable)
                .addOnSuccessListener { documentReference ->
                    println("Usuario agregado con ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    println("Error al agregar usuario: $e")
                }
        }
    )
}

fun listarMisSetas(onok: (List<Items_MisSetas>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    obtenerUsuario(
        onsuccess = { nombreUsuario ->
            db.collection("MisSetas")
                .whereEqualTo("usuario", nombreUsuario)
                .get()
                .addOnSuccessListener { documents ->
                    val misSetasList = mutableListOf<Items_MisSetas>()
                    for (document in documents) {
                        val seta = document.toObject(Items_MisSetas::class.java)
                        misSetasList.add(seta)
                    }
                    onok(misSetasList)
                }
                .addOnFailureListener { e ->
                    println("Error al listar mis setas: $e")
                    onok(emptyList()) // Si hay un error, se pasa una lista vacía
                }
        }
    )
}

fun editarSeta(seta: Items_MisSetas?) {
    val db = FirebaseFirestore.getInstance()
    // Construir una referencia a la colección "MisSetas" y realizar una consulta para obtener el documento que coincida con la fecha y la ruta de imagen
    db.collection("MisSetas")
        .whereEqualTo("fecha", seta?.fecha)
        .whereEqualTo("imagen", seta?.imagen)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                // Actualizar los campos de la seta con los nuevos valores
                document.reference.update(
                    "comentario", seta?.comentario,
                    "latitude", seta?.latitude,
                    "longitude", seta?.longitude,
                    "usuario", seta?.usuario
                )
                    .addOnSuccessListener {
                        println("Seta editada exitosamente")
                    }
                    .addOnFailureListener { e ->
                        println("Error al editar la seta: $e")
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error al buscar la seta para editar: $e")
        }
}
fun eliminarSeta(seta: Items_MisSetas?) {
    val db = FirebaseFirestore.getInstance()
    db.collection("MisSetas")
        .whereEqualTo("fecha", seta?.fecha)
        .whereEqualTo("imagen", seta?.imagen)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
                    .addOnSuccessListener {
                        println("Seta eliminada exitosamente")

                        if (seta != null) {
                            eliminarFotoStorage(seta,
                                onSuccess = {

                                    println("Foto eliminada exitosamente de Firebase Storage.")
                                },
                                onError = { exception ->

                                    println("Error al eliminar la foto: ${exception.message}")
                                }
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        println("Error al eliminar la seta: $e")
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error al buscar la seta para eliminar: $e")
        }
}

fun guardarPostCompartidos(users: List<String>, seta: Items_MisSetas?) {
    val db = FirebaseFirestore.getInstance()
    db.collection("MisSetas")
        .whereEqualTo("fecha", seta?.fecha)
        .whereEqualTo("imagen", seta?.imagen)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val postId = document.id.toString()
                val post = PostCompartidos(users, postId)
                Log.d("FireBase", "guardarPostCompartidos: $post")

                // Verificar si ya existe un post con la misma ID
                db.collection("PostCompartidos")
                    .document(postId)
                    .get()
                    .addOnSuccessListener { postDocument ->
                        if (postDocument.exists()) {
                            // Si el post ya existe, actualizar la lista de usuarios
                            val existingUsers = postDocument.toObject(PostCompartidos::class.java)?.users ?: emptyList()
                            val updatedUsers = (existingUsers + users).distinct()
                            // Actualizar el documento con la lista de usuarios actualizada
                            db.collection("PostCompartidos")
                                .document(postId)
                                .update("users", updatedUsers)
                                .addOnSuccessListener {
                                    Log.d("FireBase", "guardarPostCompartidos: Usuarios actualizados para el post existente")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FireBase", "guardarPostCompartidos: Error al actualizar usuarios en el post existente", e)
                                }
                        } else {
                            // Si el post no existe, crear uno nuevo
                            db.collection("PostCompartidos")
                                .document(postId)
                                .set(post)
                                .addOnSuccessListener {
                                    Log.d("FireBase", "guardarPostCompartidos: Nuevo post creado")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FireBase", "guardarPostCompartidos: Error al guardar el nuevo post", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FireBase", "guardarPostCompartidos: Error al verificar la existencia del post", e)
                    }
            }
            // Manejar el caso en que no se encontraron documentos
            if (documents.isEmpty) {
                Log.d("FireBase", "guardarPostCompartidos: No se encontraron documentos en 'MisSetas' que coincidan")
                // Aquí puedes realizar alguna acción adicional si es necesario
            }
        }
        .addOnFailureListener { e ->
            Log.e("FireBase", "guardarPostCompartidos: Error al realizar la consulta en 'MisSetas'", e)
        }
}
fun listarPostCompartidos(onok: (List<Items_MisSetas>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val listaPostsCompartidos = mutableListOf<Items_MisSetas>()

    obtenerUsuario { nombreUsuario ->
        Log.d("NombreUsuario", nombreUsuario)

        db.collection("PostCompartidos")
            .whereArrayContains("users", nombreUsuario)
            .get()
            .addOnSuccessListener { documents ->
                val postIds = documents.map { it.getString("idpost") ?: "" }
                Log.d("PostId", "${postIds.toString()} ")

                if (postIds.isNotEmpty()) {
                    var count = 0 // Contador para rastrear el número de documentos recuperados
                    postIds.forEach { postId ->
                        db.collection("MisSetas")
                            .document(postId)
                            .get()
                            .addOnSuccessListener { document2 ->
                                if (document2.exists()) {
                                    val post = document2.toObject(Items_MisSetas::class.java)
                                    Log.d("adsdf8asgfviauhsd", "${post.toString()} ")
                                    if (post != null) {
                                        listaPostsCompartidos.add(post)
                                    } else {
                                        Log.d("Dentro del else", "El documento $postId no existe")
                                    }
                                }
                                // Incrementar el contador
                                count++
                                // Verificar si todos los documentos se han recuperado
                                if (count == postIds.size) {
                                    // Llamar a onok cuando todos los documentos se hayan recuperado
                                    onok(listaPostsCompartidos)
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Error al obtener el documento $postId: $exception")
                                // Incrementar el contador incluso si falla la recuperación del documento
                                count++
                                // Verificar si todos los documentos se han recuperado
                                if (count == postIds.size) {
                                    // Llamar a onok cuando todos los documentos se hayan recuperado
                                    onok(listaPostsCompartidos)
                                }
                            }
                    }
                } else {
                    Log.d("Mereketenge", "listarPostCompartidos: No hay posts compartidos")
                    onok(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener la lista de posts compartidos: $exception")
                onok(emptyList()) // Llamar a onok con una lista vacía en caso de error
            }
    }
}



