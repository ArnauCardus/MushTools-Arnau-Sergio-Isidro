package com.example.mushtools.FireBase

import com.example.mushtools.models.Items_MisSetas
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
