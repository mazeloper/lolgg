package com.jschoi.develop.opgg.network

import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var instance: Retrofit? = null
    var url: String? = null

    private const val BASE_URL = "https://kr.api.riotgames.com/lol/"

    fun setIntroBaseURL(url: String): RetrofitClient {
        this.url = url
        return this
    }

    // SingleTon
    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } else if (instance?.baseUrl() != HttpUrl.get(BASE_URL)) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }


        return instance!!
    }
}