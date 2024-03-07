package com.example.mushtools.FireBase

import android.util.Log
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.models.PostCompartidos
import com.google.firebase.firestore.FirebaseFirestore

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
                // Eliminar el documento
                document.reference.delete()
                    .addOnSuccessListener {
                        println("Seta eliminada exitosamente")
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
fun guardarPostCompartidos(users: List<String>,seta: Items_MisSetas?){
    val db = FirebaseFirestore.getInstance()
    var post: PostCompartidos
    db.collection("MisSetas")
        .whereEqualTo("fecha", seta?.fecha)
        .whereEqualTo("imagen", seta?.imagen)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                post =  PostCompartidos(users,document.id.toString())
                Log.d("FireBase", "guardarPostCompartidos: ${post.toString()} ")
                db.collection("PostCompartidos")
                    .add(post)
                    .addOnSuccessListener { documentReference ->
                        Log.d("FireBase", "guardarPostCompartidos: Post creado con ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FireBase", "guardarPostCompartidos: Error al guardar el post", e)
                    }
                }
            }

}