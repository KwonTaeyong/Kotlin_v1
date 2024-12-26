package com.example.vphipda.network

import com.example.vphipda.model.Move
import com.example.vphipda.model.MoveDetail
import com.example.vphipda.model.MoveWH

data class GetMpioData(
    var state: Int,
    var message: String,
    var data: List<Move>
)


data class reqMvoutData(
    var state: Int,
    var message: String,
)


data class GetMovingData (
    var state: Int,
    var message: String,
    var data : List<MoveWH>
    )
// 창고 간 이동

data class reqMpData(
    var state: Int,
    var message: String,
)

data class getMpioDetailData (
    var state: Int,
    var message: String,
    var data: List<MoveDetail>
    )

data class mpioClsData (
    var state: Int,
    var message: String,

    )

data class TestPost (
    var state: Int,
    var message: String,
)