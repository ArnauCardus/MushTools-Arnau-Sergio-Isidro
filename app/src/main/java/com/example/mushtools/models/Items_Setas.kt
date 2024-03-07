package com.example.mushtools.models


data class Items_Setas (
    val descripcion: String = "",
    val foto: String= "",
    val nombre: String="",
    val nombrecient: String="",
    val tipo: String=""
) {
    constructor() : this("", "", "", "", "")
}