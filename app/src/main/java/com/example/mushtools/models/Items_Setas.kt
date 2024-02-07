package com.example.mushtools.models

import com.example.mushtools.R

data class Items_Setas (
    val descripcion: String = "",
    val foto: String= "",
    val nombre: String="",
    val nombrecient: String="",
    val tipo: String=""
) {
    constructor() : this("", "", "", "", "")
}