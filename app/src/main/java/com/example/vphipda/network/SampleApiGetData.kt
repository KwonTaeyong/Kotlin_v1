package com.example.vphipda.network

data class GetSampleData(
    var state: Int,
    var message: String,
    var data: List<SampleData>
)

data class SampleData(
    var NO_IO: String,
    var DT_IO: String,
    var CD_PLANT: String,
    var NM_KOR: String,
    var DC_RMK: String,
    var CD_VPLANT: String
)

data class SamplePostData(
    var state: Int,
    var message: String,
)