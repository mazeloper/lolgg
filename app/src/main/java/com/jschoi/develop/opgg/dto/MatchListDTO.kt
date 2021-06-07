package com.jschoi.develop.opgg.dto

data class MatchListDTO(
    val startIndex: Int,
    val totalGames: Int,
    val endIndex: Int,
    val matches: List<MatchReferenceDTO>
)
