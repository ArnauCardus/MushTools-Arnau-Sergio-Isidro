package com.example.mushtools.FireBase

import android.content.Context
import android.net.Uri
import com.example.mushtools.models.Items_MisSetas
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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
fun guardarFotoEnFirebaseStorage(context: Context, fotoUri: Uri, onOk: (String) -> Unit){
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val nombreImagen = "${UUID.randomUUID()}.jpg"
    val rutaImagen =  "fotos/$nombreImagen"
    val imageRef = storageRef.child(rutaImagen)
    val inputStream = context.contentResolver.openInputStream(fotoUri)
    val bytes = inputStream?.readBytes()
    bytes?.let {
        imageRef.putBytes(it)
            .addOnSuccessListener {
                onOk (rutaImagen)
            }
        println("Foto guardada exitosamente en Firebase Storage.")
    }
}
fun eliminarFotoStorage(seta: Items_MisSetas, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child(seta.imagen)

    imageRef.delete()
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}


