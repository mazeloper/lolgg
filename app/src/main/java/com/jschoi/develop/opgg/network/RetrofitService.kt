package com.jschoi.develop.opgg.network

import com.jschoi.develop.opgg.dto.LeagueDTO
import com.jschoi.develop.opgg.dto.SummonerDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET("summoner/v4/summoners/by-name/{summonerName}")
    fun reqSummonerId(
            @Path("summonerName") summonerName: String,
            @Query("api_key") api_key: String
    ): Call<SummonerDTO>

    @GET("league/v4/entries/by-summoner/{encryptedSummonerId}")
    fun reqSummonerRankInfo(
            @Path("encryptedSummonerId") encryptedSummonerId: String,
            @Query("api_key") api_key: String
    ): Call<List<LeagueDTO>>
}