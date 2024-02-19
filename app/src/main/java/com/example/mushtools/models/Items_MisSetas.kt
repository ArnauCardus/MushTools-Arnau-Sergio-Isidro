package com.example.mushtools.models

data class Items_MisSetas(
    val imagen: String,
    val comentario: String,
    val coords: Int
)
{
    constructor() : this(
        "", "", 1 )
}