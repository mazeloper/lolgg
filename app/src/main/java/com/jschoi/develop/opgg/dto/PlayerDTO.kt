package com.jschoi.develop.opgg.dto

data class PlayerDTO(
    val profileIcon: Int,
    val accountId: String,
    val matchHistoryUri: String,
    val currentAccountId: String,
    val currentPlatformId: String,
    val summonerName: String,
    val summonerId: String,
    val platformId: String,
)
