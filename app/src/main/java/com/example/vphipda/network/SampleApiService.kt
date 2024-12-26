package com.example.vphipda.network

import com.example.vphipda.model.appData
import retrofit2.Call
import retrofit2.http.*

interface SampleApiService {

    @GET("app/")
    fun get_sample(
        @Query("action") action: String,
    ) : Call<GetSampleData>

    @GET("app/")
    fun get_sampleDetail(
        @Query("action") action: String,
        @Query("no_io") no_io: String,
    ) : Call<getReceivingDetailData>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_sample(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("qt") qt: String,
        @Field("cd_lc") cd_lc: String,
        @Field("a_id") a_id: String = appData.An_ID

    ) : Call<SamplePostData>


    @FormUrlEncoded
    @POST("app/")
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    fun ins_nmreturn(
        @Field("action") action: String,
        @Field("no_io") no_io: String,
        @Field("cd_item") cd_item: String,
        @Field("qt") qt: String,
        @Field("cd_lc") cd_lc: String,
        @Field("a_id") a_id: String = appData.An_ID
    ) : Call<SamplePostData>

}