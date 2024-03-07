package com.example.mushtools.models

class PostCompartidos(
    var users: List<String> = emptyList(),
    var idpost: String = ""
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this(emptyList(), "")
}

