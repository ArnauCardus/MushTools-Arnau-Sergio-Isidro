    package com.example.mushtools.FireBase

import android.util.Log
import com.example.mushtools.models.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun obtenerUsuario(onsuccess: (String)->Unit){
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtenemos el ID del usuario actual
    db.collection("Usuarios").document(userId.toString()).get().addOnSuccessListener{result->
        if (result.exists()) {
            val usuario: Usuarios? = result.toObject(Usuarios::class.java)
            if (usuario != null) {
                onsuccess(usuario.username)
            }
        } else {
            Log.d("TAG", "No such document")
        }
    }.addOnFailureListener { exception ->
        Log.d("TAG", "get failed with ", exception)
    }
}

    fun obtenerListaUsuarios(onsuccess: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").get().addOnSuccessListener { result ->
            val listaUsuarios = mutableListOf<String>()
            for (document in result) {
                val usuario = document.toObject(Usuarios::class.java)
                listaUsuarios.add(usuario.username)
            }
            onsuccess(listaUsuarios)
        }.addOnFailureListener { exception ->
            Log.d("TAG", "Error getting documents: ", exception)
        }
    }
