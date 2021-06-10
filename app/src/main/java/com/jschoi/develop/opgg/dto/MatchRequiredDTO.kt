package com.jschoi.develop.opgg.dto

data class MatchRequiredDTO(
    val gameCreation: Long,
    val win: String,
    val championImageUrl: String,
    val spell1: String,
    val spell2: String,
    val runes1: String,
    val runes2: String
)
