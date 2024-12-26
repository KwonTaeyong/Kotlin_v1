package com.example.vphipda.network

import com.example.vphipda.model.Prod
import com.example.vphipda.model.ProdDetailItem
import com.example.vphipda.model.ProdItem

data class SearchItemData(
    var state: Int,
    var message: String,
    var data: Map<String, String>,
)

data class GetReceivingData(
    var state: Int,
    var message: String,
    var data: List<Prod>
)

data class LoadingData(
    var state: Int,
    var message: String,
)


data class getReceivingDetailData(
    var state: Int,
    var message: String,
    var data : List<ProdItem>? = null
)


data class loadingClsData(
    var state: Int,
    var message: String,
)


data class BadgeData(
    var state: Int,
    var message: String,
    var data : Map<String, Int>
)


data class GetDelRcv(
    var state: Int,
    var message: String,
)

data class GetRcvLog(
    var state: Int,
    var message: String,
    var data: List<ProdDetailItem>
)


data class GetDelRcvLog(
    var state: Int,
    var message: String,
)

data class GetSearchCode(
    var state: Int,
    var message: String,
    var data: List<SearchCodeData>
)

data class SearchCodeData(
    var CD_LC: String,
    var CD_ITEM: String,
    var QT: Int
)

data class GetVersion(
    var state: Int,
    var message: String,
    var data: String
)