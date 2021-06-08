package com.jschoi.develop.opgg.contract

import com.jschoi.develop.opgg.dto.LeagueDTO
import com.jschoi.develop.opgg.dto.MatchRequiredDTO
import com.jschoi.develop.opgg.dto.SummonerDTO

interface MainContract {

    interface View {
        fun userRankInfo(userRankData: LeagueDTO)

        fun userProfile(summonerData: SummonerDTO)

        fun showProgressBar(isVisible: Boolean)

        fun getSearchUserName(): String

        fun replaceMatchRecordList(data: MatchRequiredDTO)
    }

    interface Presenter {
        fun reqSearchSummoner(userName: String)

        fun reqSummonerRankInfo(summonerData: SummonerDTO)

        fun reqMatchDetailInfo(gameId: String)

        fun reqMatchRecordInfo()
    }
}