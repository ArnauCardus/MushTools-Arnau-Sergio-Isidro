package com.example.mushtools.models

data class Scoreboard(

    var score: Int,
    var userId: String?,
    var fecha: String
){


    constructor() : this(0, "","")
}

