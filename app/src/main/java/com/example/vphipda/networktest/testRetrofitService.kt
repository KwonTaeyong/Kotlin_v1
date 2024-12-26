package com.example.vphipda.networktest

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object testRetrofitService {
    private lateinit var retrofit: Retrofit
    fun provideRetrofit3(context: Context) : Retrofit {
        val gson = GsonBuilder()
            .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl("http://222.96.199.9:1023/")
            .client(provideOKHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .build()
        return retrofit
    }

    private fun provideOKHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    class NullOnEmptyConverterFactory: Converter.Factory(){
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type,annotations)
            return Converter<ResponseBody, Any>{
                if(it.contentLength() == 0L) return@Converter null
                delegate.convert(it)
            }
        }
    }

    fun<T> provideApi(service: Class<T>?, context: Context): T{
        return provideRetrofit3(context).create(service)
    }

}