package com.example.mushtools.FireBase

import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.models.Items_Setas
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