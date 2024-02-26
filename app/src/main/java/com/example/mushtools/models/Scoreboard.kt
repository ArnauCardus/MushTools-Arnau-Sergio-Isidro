package com.example.mushtools.models

data class Scoreboard(

    var score: Int,
    var userId: String?
){




    constructor() : this(0, "")
}

