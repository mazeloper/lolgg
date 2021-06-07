package com.jschoi.develop.opgg.dto

data class ParticipantDTO(
    val participantId: Int,
    val championId: Int,
    val runes: List<RuneDTO>,
    val stats: ParticipantStatsDTO,
    val teamId: Int,
    // val timeline: ParticipantTimelineDto,
    val spell1Id: Int,
    val spell2Id: Int,
    val highestAchievedSeasonTier: String,
    //val masteries: List<MasteryDto>,
)
