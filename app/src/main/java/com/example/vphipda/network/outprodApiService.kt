package com.example.vphipda.network

import com.example.vphipda.model.appData
import retrofit2.Call
import retrofit2.http.*

interface outprodApiService {

    // 창고 내 이동
    @GET("app/")
    fun getGizitem(
        @Query("action") action: String,
        @Query("tp") tp: String,
    ) : Call<GetGizitemData>

    @GET("app/")
    fun getTemplc(
        @Query("action") action: String,
        @Query("cd_lc") cd_lc: String,
    ) : Call<getTemplcData>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_tempqtlc(
        @Field("action") action: String,
        @Field("cd_item") cd_item: String,
        @Field("qt_lc") qt_lc: String,
        @Field("cd_lc") cd_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetInsTempqtlc>



    @GET("app/")
    fun getPkwo(
        @Query("action") action: String,
    ) : Call<GetPkwo>



    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun upd_pkwo(
        @Field("action") action: String,
        @Field("no_so") no_so: String,
        @Field("cd_item") cd_item: String,
        @Field("cd_lc") cd_lc: String,
        @Field("qt") qt: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetUpdPkwo>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_pallet(
        @Field("action") action: String,
        @Field("no_so") no_so: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetInsPallet>


    @GET("app/")
    fun getPallet(
        @Query("action") action: String,
        @Query("no_so") no_so: String,
    ) : Call<GetPallet>



    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_pkitem(
        @Field("action") action: String,
        @Field("no_so") no_so: String,
        @Field("cd_item") cd_item: String,
        @Field("no_pms") pms: String,
        @Field("qt") qt: String,
        @Field("cd_lc") cd_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetInsPkitem>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_pk(
        @Field("action") action: String,
        @Field("cd_item") cd_item: String,
        @Field("no_pms") pms: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelPkitem>



    @GET("app/")
    fun getPalletDetail(
        @Query("action") action: String,
        @Query("no_pms") pms: String,
    ) : Call<GetPalletDetail>



    @GET("app/")
    fun getWolist(
        @Query("action") action: String,
    ) : Call<GetWolist>


    @GET("app/")
    fun getWolistDetail(
        @Query("action") action: String,
        @Query("no_wo") no_wo: String,
    ) : Call<GetWolistDetail>



    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_woout(
        @Field("action") action: String,
        @Field("no_wo") no_so: String,
        @Field("cd_item") cd_item: String,
        @Field("cd_lc") cd_lc: String,
        @Field("qt") qt: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetInsWoout>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun cls_wo(
        @Field("action") action: String,
        @Field("no_wo") no_so: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetClsWo>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_pallet(
        @Field("action") action: String,
        @Field("case") case: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelPallet>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun cls_case(
        @Field("action") action: String,
        @Field("no_pms") pms: String,
        @Field("gw") gw: String,
        @Field("width") width: String,
        @Field("depth") depth: String,
        @Field("height") height: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetClsCase>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun goPort(
        @Field("action") action: String,
        @Field("no_pms") no_pms: String,
        @Field("cd_lc") cd_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetGoPort>


    @GET("app/")
    fun get_pkwo_so(
        @Query("action") action: String,
        @Query("no_so") no_so: String,
    ) : Call<GetPkwoSo>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun del_wo(
        @Field("action") action: String,
        @Field("no_wo") no_wo: String,
        @Field("cd_item") cd_item: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<GetDelWo>

}