package com.example.mushtools.FireBase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

fun obtenerUrlDeImagen(rutaImagen: String, onSuccess: (String) -> Unit) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child(rutaImagen)

    imageRef.downloadUrl
        .addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            onSuccess(imageUrl)
        }
}

