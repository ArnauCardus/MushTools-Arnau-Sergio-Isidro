package com.example.mushtools.FireBase

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun obtenerUrlDeImagen(rutaImagen: String, onSuccess: (String) -> Unit) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child(rutaImagen)

    imageRef.downloadUrl
        .addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            onSuccess(imageUrl)
        }
}