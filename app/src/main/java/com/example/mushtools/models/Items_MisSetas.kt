package com.example.mushtools.models

data class Items_MisSetas(
    val imagen: String,
    val comentario: String,
    val latitude: String,
    val longitude: String
)
{
    constructor() : this(
        "", "","","")
}