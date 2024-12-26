package com.example.vphipda.model

data class Prod (
    var DT_IO: String,
    var LN_PARTNER:String,
    var NO_IO: String
)

data class ProdItem (
    var CD_ITEM: String,
    var QT_IO: Int,
    var QT_LC: Int,
)

data class ProdDetailItem (

    var CD_LC:String,
    var CD_ITEM:String,
    var QT_LC:Int,
    var DTS_LC:String

        )