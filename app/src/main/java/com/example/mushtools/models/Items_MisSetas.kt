package com.example.mushtools.models

data class Items_MisSetas(
    val imagen: String,
    val comentario: String,
    val fecha: String,
    val latitude: Double?,
    val longitude: Double?,
    val usuario: String
)
{
    constructor() : this(
        "", "","",null,null,"")
}