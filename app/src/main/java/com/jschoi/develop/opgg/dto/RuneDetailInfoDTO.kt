package com.jschoi.develop.opgg.dto

import com.google.gson.annotations.SerializedName

data class RuneDetailInfoDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("key") val key: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("name") val name: String,
    @SerializedName("slots") val slots: List<Slot>
)

data class Slot(
    @SerializedName("runes") val runes: List<Runes>
)

data class Runes(
    @SerializedName("id") val id: Int,
    @SerializedName("key") val key: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("name") val name: String,
    @SerializedName("shortDesc") val shortDesc: String,
    @SerializedName("longDesc") val longDesc: String
)
