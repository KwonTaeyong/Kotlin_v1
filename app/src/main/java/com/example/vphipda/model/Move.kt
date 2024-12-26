package com.example.vphipda.model

data class Move (
    var NO_IO: String,
    var DT_IO:String,
    var I_SL: String,
    var O_SL: String
)


data class MoveWH (
    var CD_ITEM: String,
    var QT_LC: Int
)

data class MoveDetail (
    var CD_ITEM: String,
    var QT_IO: Int,
    var QT_LC: Int
        )

