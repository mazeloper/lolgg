package com.jschoi.develop.opgg.dto

data class MatchReferenceDTO(
    val gameId: Long,
    val role: String,
    val season: Int,
    val platformId: String,
    val champion: Int,
    val queue: Int,
    val lane: String,
    val timestamp: Long
)
