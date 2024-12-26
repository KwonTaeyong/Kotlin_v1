package com.example.vphipda.network



data class invenitem(
    var NO_CNT: String,
    var CD_LC: String,
    var CD_ITEM: String,
    var QT_CNT: Int,
    var DTS_CNT: String
)

data class GetChkQtcnt(
    var state: Int,
    var message: String,
    var data: List<Map<String, String>>
)


data class GetInsQtcnt (
    var state: Int,
    var message: String

        )

data class GetQtcnt (
    var state: Int,
    var message: String,
    var data: List<invenitem>
        )


data class GetDelQtcnt (
    var state: Int,
    var message: String,
        )