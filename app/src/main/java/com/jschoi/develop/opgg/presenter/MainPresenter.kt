package com.jschoi.develop.opgg.presenter

import android.content.Context
import com.jschoi.develop.opgg.Config
import com.jschoi.develop.opgg.Config.DATA_RUNES_IMAGE_URL
import com.jschoi.develop.opgg.R
import com.jschoi.develop.opgg.contract.MainContract
import com.jschoi.develop.opgg.dto.*
import com.jschoi.develop.opgg.network.RetrofitClient
import com.jschoi.develop.opgg.network.RetrofitService
import com.jschoi.develop.opgg.util.LogUtil
import com.jschoi.develop.opgg.util.Util
import com.jschoi.develop.opgg.view.activity.IntroActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainPresenter(private var view: MainContract.View, private var context: Context) :
    MainContract.Presenter {

    private var retrofit: Retrofit = RetrofitClient.getInstance()
    private var riotApiService: RetrofitService = retrofit.create(RetrofitService::class.java)
    private lateinit var encryptedAccountId: String

    private var searchUserIndex = 0

    // TODO DTO에 담을 데이터들
    private lateinit var win: String
    private lateinit var championImageUrl: String
    private lateinit var spell1: String
    private lateinit var spell2: String
    private lateinit var rune1: String
    private lateinit var rune2: String

    private var matchRequiredList = arrayListOf<MatchRequiredDTO>()

    /**
     * API 통신 - UserID 검색
     */
    override fun reqSearchSummoner(userName: String) {
        view.showProgressBar(true)

        riotApiService.reqSummonerId(userName, context.getString(R.string.riot_api_key))
            .enqueue(object : Callback<SummonerDTO> {
                override fun onResponse(call: Call<SummonerDTO>, response: Response<SummonerDTO>) {

                    if (response.isSuccessful.not()) {
                        view.showProgressBar(false)
                        return
                    }
                    // 유저 ID -> 리그 API 호출
                    response.body()?.let {
                        encryptedAccountId = it.accountId

                        view.userProfile(it)
                        reqSummonerRankInfo(it)
                        reqMatchRecordInfo()
                        return
                    }

                    view.showProgressBar(false)
                }

                override fun onFailure(call: Call<SummonerDTO>, t: Throwable) {
                    view.showProgressBar(false)
                    Util.showDialogMessage(context, t.toString())
                }
            })
    }

    /**
     * API 통신 - UserID 매칭 랭크 정보
     */
    override fun reqSummonerRankInfo(summonerData: SummonerDTO) {
        val userId = summonerData.id
        riotApiService.reqSummonerRankInfo(userId, context.getString(R.string.riot_api_key))
            .enqueue(object : Callback<List<LeagueDTO>> {
                override fun onResponse(
                    call: Call<List<LeagueDTO>>,
                    response: Response<List<LeagueDTO>>
                ) {
                    if (response.isSuccessful.not()) return

                    // 유저 ID -> 리그 API 호출
                    response.body()?.let { rankData ->
                        view.userRankInfo(rankData.first())
                    }
                }

                override fun onFailure(call: Call<List<LeagueDTO>>, t: Throwable) {
                    view.showProgressBar(false)
                    Util.showDialogMessage(context, t.toString())
                }
            })
    }

    /**
     * API 통신 - User 전적 리스트
     */
    override fun reqMatchRecordInfo() {
        riotApiService.reqMatchRecordInfo(
            encryptedAccountId,
            context.getString(R.string.riot_api_key)
        )
            .enqueue(object : Callback<MatchListDTO> {
                override fun onResponse(
                    call: Call<MatchListDTO>,
                    response: Response<MatchListDTO>
                ) {

                    if (response.isSuccessful.not() || response.body() == null) {
                        view.showProgressBar(false)
                        return
                    }

                    response.body()?.let {
                        it.matches.forEachIndexed { index, data ->
                            // TODO 속도 이슈 생각
                            if (index > 0) return@forEachIndexed
                            reqMatchDetailInfo(data.gameId.toString())

                        }
                    }
                }

                override fun onFailure(call: Call<MatchListDTO>, t: Throwable) {
                    view.showProgressBar(false)
                    Util.showDialogMessage(context, t.toString())
                }
            })
    }

    /**
     * API 통신 - 검색유저 전적정보
     */
    override fun reqMatchDetailInfo(gameId: String) {
        riotApiService.reqMatchDetailInfo(gameId, context.getString(R.string.riot_api_key))
            .enqueue(object : Callback<MatchDTO> {
                override fun onResponse(call: Call<MatchDTO>, response: Response<MatchDTO>) {
                    view.showProgressBar(false)

                    if (response.isSuccessful.not()) return

                    response.body()?.let {
                        matchDataParse(it)
                    }
                }

                override fun onFailure(call: Call<MatchDTO>, t: Throwable) {
                    view.showProgressBar(false)
                    Util.showDialogMessage(context, t.toString())
                }
            })
    }

    private fun matchDataParse(item: MatchDTO) {
        // 검색한 유저에 정보 인덱스
        item.participantIdentities.forEachIndexed { index, data ->
            if (view.getSearchUserName() == ((data.player.summonerName).trim().toLowerCase())) {
                searchUserIndex = index
                return@forEachIndexed
            }
        }
        // 승패확인
        win = if (item.participants[searchUserIndex].stats.win) "승리" else "패배"

        // 챔피언 이미지
        val id = item.participants[searchUserIndex].championId.toString()
        val championInfo = IntroActivity.CHAMPION_LIST[id] as JSONObject
        val championImage = championInfo["image"] as JSONObject
        championImageUrl =
            "${IntroActivity.getCurrentVersionUrl()}${Config.CHAMPION_INFO_URL}${championImage["full"]}"

        // 스펠 이미지
        val spell1Id = item.participants[searchUserIndex].spell1Id.toString()
        val spell2Id = item.participants[searchUserIndex].spell2Id.toString()
        val spellBaseUrl = "${IntroActivity.getCurrentVersionUrl()}${Config.SPELL_IMAGE_URL}"
        spell1 = "${spellBaseUrl}${IntroActivity.SPELL_LIST[spell1Id]?.get("id")}.png"
        spell2 = "${spellBaseUrl}${IntroActivity.SPELL_LIST[spell2Id]?.get("id")}.png"
        // TODO 메인 룬 이미지
        // https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles/<style_name>.png
        // https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles/ Resolve/GraspOfTheUndying/GraspOfTheUndying.png
        // 8437
        val rune1Id = item.participants[searchUserIndex].stats.perkPrimaryStyle.toString()
        val runes = IntroActivity.RUNES_LIST[rune1Id]?.getJSONObject(0)?.getJSONArray("runes")!!
        val rune2Id = item.participants[searchUserIndex].stats.perkSubStyle.toString()
        var run1Image: String?
        for (idx in 0 until runes.length()) {
            if (runes.getJSONObject(idx)["id"] == item.participants[searchUserIndex].stats.perk0) {
                run1Image = runes.getJSONObject(idx)["icon"].toString()
                rune1 = "${DATA_RUNES_IMAGE_URL}${run1Image}"
                break
            }
        }
        // 룬 이미지2
        IntroActivity.RUNES_SIMPLE_INFO.forEach {

            LogUtil.information("#################### ${it["id"]}")
            LogUtil.information("##########rune2Id########## ${rune2Id}")
            if (it["id"] == rune2Id) {
                LogUtil.warning("####################")
                rune2 = "${DATA_RUNES_IMAGE_URL}${it["icon"]}"
                return@forEach
            }
        }

        LogUtil.error(">>>>>>> MAIN : ${rune1}")
        LogUtil.error(">>>>>>> SUB : ${rune2}")
        view.replaceMatchRecordList(MatchRequiredDTO(win, championImageUrl, spell1, spell2))
    }
}