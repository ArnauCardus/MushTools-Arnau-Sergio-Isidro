package com.example.mushtools.models

data class Scoreboard(

    val score: Int ,
    val usuario : String
){
    constructor() : this(0, "" )
}

