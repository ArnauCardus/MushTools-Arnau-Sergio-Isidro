package com.example.mushtools.FireBase

import android.content.ContentValues
import android.util.Log
import com.example.mushtools.models.Scoreboard
import com.google.firebase.firestore.FirebaseFirestore


fun GuardarScore(score:Int) {
    val db = FirebaseFirestore.getInstance()
    var nombreUsuario: String
    obtenerUsuario(
        onsuccess = { nombre ->
            nombreUsuario = nombre
            var ScoreUser = Scoreboard(score, nombreUsuario)
            var ScoreLista: MutableList<Scoreboard>
            ListarScore(
                onok = { listSetas ->
                    ScoreLista = listSetas.toMutableList()
                    // Verificar si el nombre de usuario ya existe en la lista
                    val existingScoreboard = ScoreLista.find { it.userId == ScoreUser.userId }

                    if (existingScoreboard != null) {
                        if (ScoreUser.score > existingScoreboard.score) {
                            // El nuevo puntaje es mayor, actualizar el puntaje en Firebase
                            existingScoreboard.score = ScoreUser.score

                            // Realizar una consulta para encontrar el documento que coincide con el usuario
                            db.collection("Scoreboard")
                                .whereEqualTo("userId", existingScoreboard.userId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // Obtener el documentId del documento encontrado
                                        val documentId = document.id

                                        // Actualizar el puntaje en el documento encontrado
                                        db.collection("Scoreboard").document(documentId)
                                            .update("score", ScoreUser.score)
                                            .addOnSuccessListener {
                                                Log.d("Compr", "Puntaje actualizado para ${ScoreUser.userId} en Firestore")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("Compr", "Error al actualizar el puntaje para ${ScoreUser.userId} en Firestore", e)
                                            }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("Compr", "Error al buscar el documento en Firestore", exception)
                                }
                        } else {
                            Log.d("Compr", "Puntaje no actualizado para ${ScoreUser.userId} - El nuevo puntaje no es mayor")
                        }
                        Log.d("Compr", "Repe: ")

                    } else {
                        Log.d("Compr", "No repe: ")
                        db.collection("Scoreboard")
                            .add(ScoreUser)
                    }
                }
            )

        }
    )

}




fun ListarScore(onok: (List<Scoreboard>) -> Unit){
    val db = FirebaseFirestore.getInstance()
    val ScoreLista = mutableListOf<Scoreboard>()
    db.collection("Scoreboard").get().addOnSuccessListener { result ->
        for (document in result) {
            val score: Scoreboard = document.toObject(Scoreboard::class.java)
            ScoreLista.add(score)

        }
        onok(ScoreLista)
    }
        .addOnFailureListener { exception ->
            Log.d(ContentValues.TAG, "Error getting documents: ", exception)
        }
}
