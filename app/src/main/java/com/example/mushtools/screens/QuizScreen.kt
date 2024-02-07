package com.example.mushtools.screens

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mushtools.models.Items_Setas
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

@Composable
fun Quiz(){
    val db = FirebaseFirestore.getInstance()
    Column (
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(text = "Quiz")

        val Setaslista : MutableList<Items_Setas> = mutableListOf<Items_Setas>()



        db.collection("Setas").get() .addOnSuccessListener { result ->
                for (document in result) {
                    val seta : Items_Setas = document.toObject(Items_Setas::class.java)
                    Setaslista.add(seta)
                    Log.d("Setas","$Setaslista")
                    //Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}