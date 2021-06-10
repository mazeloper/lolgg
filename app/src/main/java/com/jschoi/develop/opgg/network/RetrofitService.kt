package com.jschoi.develop.opgg.network

import com.jschoi.develop.opgg.dto.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    /**
     * 유저 사용자 정보
     */
    @GET("summoner/v4/summoners/by-name/{summonerName}")
    fun reqSummonerId(
        @Path("summonerName") summonerName: String,
        @Query("api_key") api_key: String
    ): Call<SummonerDTO>

    /**
     * 유저 랭크정보
     */
    @GET("league/v4/entries/by-summoner/{encryptedSummonerId}")
    fun reqSummonerRankInfo(
        @Path("encryptedSummonerId") encryptedSummonerId: String,
        @Query("api_key") api_key: String
    ): Call<List<LeagueDTO>>

    /**
     * 전적 리스트 API
     */
    @GET("match/v4/matchlists/by-account/{encryptedAccountId}")
    fun reqMatchRecordInfo(
        @Path("encryptedAccountId") encryptedAccountId: String,
        @Query("api_key") api_key: String
    ): Call<MatchListDTO>

    /**
     * 전적 리스트 API
     */
    @GET("match/v4/matches/{matchId}")
    fun reqMatchDetailInfo(
        @Path("matchId") matchId: String,
        @Query("api_key") api_key: String
    ): Call<MatchDTO>

    /**
     * 전적 리스트 API
     */
    @GET("data/ko_KR/runesReforged.json")
    fun reqRunInfo(): Call<List<RuneDetailInfoDTO>>
}