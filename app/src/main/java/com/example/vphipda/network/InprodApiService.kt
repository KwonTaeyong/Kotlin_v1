package com.example.vphipda.network

import com.example.vphipda.model.appData
import retrofit2.Call
import retrofit2.http.*

interface InprodApiService {

    @GET("app/")
    fun getReceiving(
        @Query("action") action: String,
    ) : Call<GetReceivingData>

    @GET("app/")
    fun searchItem(
        @Query("action") action: String,
        @Query("scancode") scancode: String
    ) : Call<SearchItemData>



    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun loading(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("cd_lc") cd_lc: String,
        @Field("qt_lc") qt_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<LoadingData>


    @GET("app/")
    fun getReceivingDetail(
        @Query("action") action: String,
        @Query("no_io") no_io: String
    ) : Call<getReceivingDetailData>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun loadingCls(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<loadingClsData>

    @GET("app/")
    fun Badge (
        @Query("action") action: String
    ) : Call<BadgeData>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_rcv(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelRcv>


    @GET("app/")
    fun get_rcv_log (
        @Query("action") action: String,
        @Query("no_io") no_io: String,
        @Query("cd_item") cd_item: String,
    ) : Call<GetRcvLog>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_rcv_log(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("dts_lc") dts_lc: String,
        @Field("cd_lc") cd_lc: String,
        @Field("qt_lc") qt_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelRcvLog>


    @GET("app/")
    fun search_code (
        @Query("action") action: String,
        @Query("scancode") scancode: String,
    ) : Call<GetSearchCode>

    @GET("app/")
    fun get_version (
        @Query("action") action: String,
    ) : Call<GetVersion>


}

