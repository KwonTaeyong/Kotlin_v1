package com.example.vphipda.network


import com.example.vphipda.model.appData
import retrofit2.Call
import retrofit2.http.*

interface MoveApiService {
    // 창고 내 이동
    @GET("app/")
    fun getMpio(
        @Query("action") action: String,
    ) : Call<GetMpioData>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun reqMvout(
        @Field("action") action: String,
        @Field("cd_item") cd_item: String,
        @Field("qt_lc") qt_ld: String,
        @Field("cd_lc1") cd_lc1: String,
        @Field("cd_lc2") cd_lc2: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<reqMvoutData>

    @GET("app/")
    fun getMoving(
        @Query("action") action: String,
        @Query("cd_lc") cd_lc: String,
    ) : Call<GetMovingData>

    // 창고 간 이동


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun reqMp(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("qt_lc") qt_ld: String,
        @Field("cd_lc1") cd_lc1: String,
        @Field("cd_lc2") cd_lc2: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<reqMpData>

    @GET("app/")
    fun getMpioDetail(
        @Query("action") action: String,
        @Query("no_io") no_io: String,
    ) : Call<getMpioDetailData>

    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun mpioCls (
        @Query("action") action: String,
        @Query("no_io") no_io: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<mpioClsData>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun testPost(
        @Field("action") action: String,
        @Field("key1") key1: String,
        @Field("key2") key2: String,
        @Field("key3") key3: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<TestPost>

}