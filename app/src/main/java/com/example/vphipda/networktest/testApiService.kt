package com.example.vphipda.networktest

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface testApiService {

    @GET("testscan")
    fun getMinuDustWeekFrcstDspth(
        @Query("code") code: String
    ) : Call<ResultGetSearch>

}
