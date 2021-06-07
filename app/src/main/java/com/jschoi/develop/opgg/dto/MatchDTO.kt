package com.jschoi.develop.opgg.dto

data class MatchDTO(
    val gameId: Long,
    val participantIdentities: List<ParticipantIdentityDTO>,
    val queueId: Int,
    val gameType: String,
    val gameDuration: Long,
    val teams: List<TeamStatsDTO>,
    val platformId: String,
    val gameCreation: Long,
    val seasonId: Int,
    val gameVersion: String,
    val mapId: Int,
    val gameMode: String,
    val participants: List<ParticipantDTO>
)