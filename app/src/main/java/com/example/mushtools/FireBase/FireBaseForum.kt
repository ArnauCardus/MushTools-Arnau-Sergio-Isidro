package com.example.mushtools.FireBase

import com.example.mushtools.models.Publicaciones
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

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
}