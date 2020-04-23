package com.codingwithmitch.rxjavaflatmapexample.requests


import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {

    val BASE_URL = "https://jsonplaceholder.typicode.com"

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = retrofitBuilder.build()

    val requestApi = retrofit.create(RequestApi::class.java)
}


//class ServiceGenerator {
//
//    companion object{
//
//
//        val BASE_URL = "https://jsonplaceholder.typicode.com"
//
//        private val retrofitBuilder = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//
//        private val retrofit = retrofitBuilder.build()
//
//
//        fun getRequestApi(): RequestApi {
//            return retrofit.create(RequestApi::class.java)
//        }
//
//    }
//}
