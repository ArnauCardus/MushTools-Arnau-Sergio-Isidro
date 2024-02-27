package com.example.mushtools.models




data class Publicaciones(
    val id: String = "",
    val titulo: String = "",
    val contenido: String = "",
    var comentarios: List<String> = listOf(),
    var nombreUsuario: String = "",
    val fecha : String = ""
)
