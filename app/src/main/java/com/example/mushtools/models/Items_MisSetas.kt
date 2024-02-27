package com.example.mushtools.models

data class Items_MisSetas(
    val imagen: String,
    val comentario: String,
    val fecha: String,
    val latitude: String,
    val longitude: String,
    val usuario: String
)
{
    constructor() : this(
        "", "","","","","")
}