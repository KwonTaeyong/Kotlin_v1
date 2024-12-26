package com.example.vphipda.network


data class GetGizitemData(
    var state: Int,
    var message: String,
    var data: List<Gizitem>
)

data class Gizitem(
    var CD_ITEM: String,
    var QT_IO: Int
)


data class getTemplcData(
    var state: Int,
    var message: String,
    var data: List<TemplcData>
)


data class TemplcData (
    var CD_ITEM: String,
    var QT_LC: String,
        )

data class GetInsTempqtlc(
    var state: Int,
    var message: String,
)


data class GetPkwo(
    var state: Int,
    var message: String,
    var data: List<Pkwo>
)


data class Pkwo (
    var NO_SO: String,
    var LN_PARTNER: String,
    var NM_KOR: String
        )

data class GetUpdPkwo (
    var state: Int,
    var message: String
        )

data class GetInsPallet (
    var state: Int,
    var message: String
)

data class GetPallet (
    var state: Int,
    var message: String,
    var data: List<Pallet>
        )

data class Pallet (
    var NO_PMS: String,
    var P_PVLHCASE: String,
    var NO_SO: String
        )

data class GetInsPkitem (
    var state: Int,
    var message: String
        )

data class GetDelPkitem(
    var state: Int,
    var message: String

)

data class GetPalletDetail (
    var state: Int,
    var message: String,
    var data: List<PalletDetail>
        )

data class PalletDetail (
    var CD_ITEM: String,
    var QT: Int,
        )


data class GetWolist (
    var state: Int,
    var message: String,
    var data: List<Wolist>
)

data class Wolist (
    var NO_WO: String,
    var DT_WL: String,
        )

data class GetWolistDetail (
    var state: Int,
    var message: String,
    var data: List<WolistDetail>
        )


data class GetPkwoSo (
    var state: Int,
    var message: String,
    var data: List<PkwoSo>
)

data class PkwoSo(
    var CD_ITEM: String,
    var QT_WO: Int,
    var QT_PK: Int
)

data class WolistDetail (

    var CD_ITEM: String,
    var QT_IO: Int,
    var QT_WO: Int

        )


data class GetInsWoout (
    var state: Int,
    var message: String,
        )

data class GetClsWo (
    var state: Int,
    var message: String,
        )

data class GetDelPallet (
    var state: Int,
    var message: String,
        )

data class GetClsCase (
    var state: Int,
    var message: String

        )

data class GetGoPort(
    var state: Int,
    var message: String
)

data class GetDelWo(
    var state: Int,
    var message: String
)