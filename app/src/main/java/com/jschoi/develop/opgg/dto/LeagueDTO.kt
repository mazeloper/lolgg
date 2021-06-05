package com.jschoi.develop.opgg.dto

data class LeagueDTO(
        val leagueId: String,
        val summonerId: String,
        val summonerName: String,
        val queueType: String,
        val tier: String,
        val rank: String,
        val leaguePoints: Int,
        val wins: Int,
        val losses: Int,
        val hotStreak: Boolean,
        val veteran: Boolean,
        val freshBlood: Boolean,
        val inactive: Boolean
       // val LeagueEntryDTO: List<LeagueEntryDTO>
)
//
//data class LeagueEntryDTO(
//
//)