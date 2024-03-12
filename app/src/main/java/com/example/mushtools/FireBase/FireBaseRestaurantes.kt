package com.example.mushtools.FireBase

import com.example.mushtools.models.Restaurantes



import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RestaurantesRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getRestaurantes(): List<Restaurantes> {
        val restaurantesList = mutableListOf<Restaurantes>()
        val querySnapshot = db.collection("Restaurantes").get().await()
        for (document in querySnapshot.documents) {
            val nombre = document.getString("nombre") ?: ""
            val latitude = document.getString("latitude") ?: ""
            val longitude = document.getString("longitude") ?: ""
            restaurantesList.add(Restaurantes(nombre, latitude, longitude))
        }
        return restaurantesList
    }
}


