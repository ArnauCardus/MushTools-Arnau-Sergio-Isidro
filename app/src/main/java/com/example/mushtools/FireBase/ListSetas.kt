package com.example.mushtools.FireBase

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.mushtools.models.Items_MisSetas
import com.example.mushtools.models.Items_Setas
import com.google.firebase.firestore.FirebaseFirestore

fun ListarSetas(onok: (List<Items_Setas>) -> Unit){
    val db = FirebaseFirestore.getInstance()
    val Setaslista = mutableListOf<Items_Setas>()
    db.collection("Setas").get().addOnSuccessListener { result ->
        for (document in result) {
            val seta: Items_Setas = document.toObject(Items_Setas::class.java)
            Setaslista.add(seta)
        }
        onok(Setaslista)
    }
        .addOnFailureListener { exception ->
            Log.d(ContentValues.TAG, "Error getting documents: ", exception)
        }
}