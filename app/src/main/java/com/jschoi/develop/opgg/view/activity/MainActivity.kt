package com.jschoi.develop.opgg.view.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jschoi.develop.opgg.Config.PROFILE_ICON_URL
import com.jschoi.develop.opgg.R
import com.jschoi.develop.opgg.adapter.MatchRecordAdapter
import com.jschoi.develop.opgg.databinding.ActivityMainBinding
import com.jschoi.develop.opgg.dto.*
import com.jschoi.develop.opgg.enum.Tier
import com.jschoi.develop.opgg.network.RetrofitClient
import com.jschoi.develop.opgg.network.RetrofitService
import com.jschoi.develop.opgg.util.LogUtil
import com.jschoi.develop.opgg.util.Util
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {
    // TODO 버전체크 추가 필요
    // https://ddragon.leagueoflegends.com/api/versions.json'

    private lateinit var userName: String
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var riotApiService: RetrofitService
    private lateinit var encryptedAccountId: String
    private lateinit var matchRecordAdapter: MatchRecordAdapter

    private var matchList = arrayListOf<MatchDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


        initRetrofit()
        initViews()
        initEventLisener()

    }

    private fun initViews() {
        matchRecordAdapter = MatchRecordAdapter(this)
        mainBinding.matchListRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.matchListRecyclerView.adapter = matchRecordAdapter
    }

    private fun initEventLisener() {
        // 검색 EditText OnEditorAction
        mainBinding.userNameEditText.setOnEditorActionListener { _, action, _ ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 내리기
                mainBinding.saerchButton.performClick()
                handled = true
            }
            handled
        }
        // 검색 Button OnClick
        mainBinding.saerchButton.setOnClickListener {

            hideSoftKeyboard()
            if (mainBinding.userNameEditText.text.isNotEmpty()) {
                reqSearchSummoner()
            }
        }
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        riotApiService = retrofit.create(RetrofitService::class.java)


    }

    /**
     * 키보드 내리기
     */
    private fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(mainBinding.userNameEditText.windowToken, 0)
    }

    /**
     * 유저 프로필
     */
    private fun userProfile(summonerData: SummonerDTO) {
        // 유저 프로필 아이콘
        summonerData.profileIconId.let { iconId ->
            val profileIconImageUrl = "${PROFILE_ICON_URL}${iconId}.png"
            setImageViewToGlide(mainBinding.userIconImageView, profileIconImageUrl)
        }
        mainBinding.userLevelTextView.text = summonerData.summonerLevel.toString()  // 레벨
        mainBinding.userLevelTextView.isVisible = true

        mainBinding.userNameTextView.text = summonerData.name                       // 유저명

    }

    /**
     * 유저 티어정보
     */
    private fun userRankInfo(userRankData: LeagueDTO) {
        // 티어이미지
        userRankImageBinding(userRankData.tier)

        mainBinding.queueTypeTextView.text = when (userRankData.queueType) {
            // TODO 추가필요
            "RANKED_SOLO_5x5" -> "솔로랭크"
            else -> ""
        }
        mainBinding.tierTextView.text = userRankData.tier
        mainBinding.leaguePointTextView.text = String.format("%dLP", userRankData.leaguePoints)
        mainBinding.winsTextView.text = String.format("/ %d승", userRankData.wins)
        mainBinding.lossesTextView.text = String.format("%d패", userRankData.losses)
        val total = (userRankData.wins + userRankData.losses).toDouble()
        val winRate = (userRankData.wins / total * 100).toInt()
        mainBinding.winRateTextView.text = String.format("승률 %d%%", winRate)

    }

    /**
     * 유저 랭크 이미지
     */
    private fun userRankImageBinding(tier: String) {
        val imageResId = when (tier) {
            Tier.BRONZE.name -> {
                R.drawable.emblem_bronze
            }
            Tier.SILVER.name -> {
                R.drawable.emblem_silver
            }
            Tier.GOLD.name -> {
                R.drawable.emblem_gold
            }
            Tier.PLATINUM.name -> {
                R.drawable.emblem_platinum
            }
            Tier.DIAMOND.name -> {
                R.drawable.emblem_diamond
            }
            Tier.MASTER.name -> {
                R.drawable.emblem_master
            }
            Tier.GRANDMASTER.name -> {
                R.drawable.emblem_grandmaster
            }
            Tier.CHALLENGER.name -> {
                R.drawable.emblem_challenger
            }
            else -> {
                // TODO Unranked Image 필요
                R.drawable.ic_launcher_background
            }
        }
        setImageViewToGlide(mainBinding.rankImageView, imageResId)
    }

    /**
     * ImageView Glide 적용
     */
    private fun <T> setImageViewToGlide(view: ImageView, image: T) {
        Glide.with(this)
            .load(image)
            .centerCrop()
            .into(view);
    }

    /**
     * API 통신 - UserID 검색
     */
    private fun reqSearchSummoner() {
        showProgressBar(true)

        userName = mainBinding.userNameEditText.text.toString()

        riotApiService.reqSummonerId(userName, getString(R.string.riot_api_key))
            .enqueue(object : Callback<SummonerDTO> {
                override fun onResponse(call: Call<SummonerDTO>, response: Response<SummonerDTO>) {

                    if (response.isSuccessful.not()) {
                        showProgressBar(false)
                        return
                    }
                    // LogUtil.warning(">>>>>>>>>>${response.body()}")
                    // 유저 ID -> 리그 API 호출
                    response.body()?.let {
                        encryptedAccountId = it.accountId

                        userProfile(it)
                        reqSummonerRankInfo(it)
                        reqMatchRecordInfo()
                        return
                    }

                    showProgressBar(false)
                }

                override fun onFailure(call: Call<SummonerDTO>, t: Throwable) {
                    showProgressBar(false)
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }
            })
    }

    fun getSearchUserName(): String {
        return userName.toLowerCase()
    }

    /**
     * API 통신 - UserID 매칭 랭크 정보
     */
    private fun reqSummonerRankInfo(summonerData: SummonerDTO) {
        val userId = summonerData.id
        riotApiService.reqSummonerRankInfo(userId, getString(R.string.riot_api_key))
            .enqueue(object : Callback<List<LeagueDTO>> {
                override fun onResponse(
                    call: Call<List<LeagueDTO>>,
                    response: Response<List<LeagueDTO>>
                ) {
                    if (response.isSuccessful.not()) return

                    // 유저 ID -> 리그 API 호출
                    response.body()?.let { rankData ->
                        // LogUtil.error(response.body().toString())
                        userRankInfo(rankData.first())
                    }
                }

                override fun onFailure(call: Call<List<LeagueDTO>>, t: Throwable) {
                    showProgressBar(false)
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }
            })
    }

    /**
     * API 통신 - User 전적 리스트
     */
    private fun reqMatchRecordInfo() {
        riotApiService.reqMatchRecordInfo(encryptedAccountId, getString(R.string.riot_api_key))
            .enqueue(object : Callback<MatchListDTO> {
                override fun onResponse(
                    call: Call<MatchListDTO>,
                    response: Response<MatchListDTO>
                ) {

                    if (response.isSuccessful.not() || response.body() == null) {
                        showProgressBar(false)
                        return
                    }

                    response.body()?.let {
                        // matchList = it.matches.toMutableList()
                        it.matches.forEachIndexed { index, data ->
                            // TODO 속도 이슈 생각
                            //  if (index > 2) return@forEachIndexed
                            reqMatchDetailInfo(data.gameId.toString())

                        }
                        // matchRecordAdapter.replaceList(matchList)

                    }
                }

                override fun onFailure(call: Call<MatchListDTO>, t: Throwable) {
                    showProgressBar(false)
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }
            })
    }

    private fun reqMatchDetailInfo(gameId: String) {
        riotApiService.reqMatchDetailInfo(gameId, getString(R.string.riot_api_key))
            .enqueue(object : Callback<MatchDTO> {
                override fun onResponse(call: Call<MatchDTO>, response: Response<MatchDTO>) {
                    showProgressBar(false)

                    if (response.isSuccessful.not()) return

                    response.body()?.let {
                        // val bc = it.toString().split(",")
                        // bc.forEach { bc2 ->
                        //     LogUtil.warning(">>>> data : ${bc2.toString()}")

                        // }
                        matchList.add(it)
                        matchRecordAdapter.replaceList(matchList)
                    }
                }

                override fun onFailure(call: Call<MatchDTO>, t: Throwable) {
                    showProgressBar(false)
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }
            })
    }

    /**
     * ProgressBar Show / Hide
     */
    private fun showProgressBar(isVisible: Boolean) {
        mainBinding.progressBar.isVisible = isVisible
    }
}