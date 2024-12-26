package com.example.vphipda.network

import com.example.vphipda.model.appData
import retrofit2.Call
import retrofit2.http.*

interface InvenApiService {

    @GET("app/")
    fun chk_qtcnt(
        @Query("action") action: String,
    ) : Call<GetChkQtcnt>

    @GET("app/")
    fun get_qtcnt(
        @Query("action") action: String,
        @Query("no_cnt") no_cnt: String,
        @Query("cd_lc") cd_lc: String,
    ) : Call<GetQtcnt>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_qtcnt(
        @Field("action") action: String,
        @Field("no_cnt") no_cnt: String,
        @Field("cd_lc") cd_lc: String,
        @Field("cd_item") cd_item: String,
        @Field("qt_cnt") qt_cnt: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetInsQtcnt>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_qtcnt(
        @Field("action") action: String,
        @Field("no_cnt") no_cnt: String,
        @Field("cd_lc") cd_lc: String,
        @Field("cd_item") cd_item: String,
        @Field("dts_cnt") dts_cnt: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelQtcnt>

}