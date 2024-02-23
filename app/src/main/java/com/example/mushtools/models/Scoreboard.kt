package com.example.mushtools.models

data class Scoreboard(

    val score: Int,
    var userId : String
){
    constructor() : this(0, "")
}

