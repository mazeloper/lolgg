package com.jschoi.develop.opgg.view.activity

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jschoi.develop.opgg.Config.PROFILE_ICON_URL
import com.jschoi.develop.opgg.R
import com.jschoi.develop.opgg.adapter.MatchRecordAdapter
import com.jschoi.develop.opgg.contract.MainContract
import com.jschoi.develop.opgg.databinding.ActivityMainBinding
import com.jschoi.develop.opgg.dto.LeagueDTO
import com.jschoi.develop.opgg.dto.MatchDTO
import com.jschoi.develop.opgg.dto.MatchRequiredDTO
import com.jschoi.develop.opgg.dto.SummonerDTO
import com.jschoi.develop.opgg.enum.Tier
import com.jschoi.develop.opgg.presenter.MainPresenter

class MainActivity : BaseActivity(), MainContract.View {

    // TODO 버전체크 추가 필요
    // https://ddragon.leagueoflegends.com/api/versions.json'


    private lateinit var presenter: MainContract.Presenter
    private lateinit var userName: String
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var matchRecordAdapter: MatchRecordAdapter

    private var matchList = arrayListOf<MatchDTO>()
    private var matchRequiredList = arrayListOf<MatchRequiredDTO>()

    /**
     * set Layout Viewq
     */
    override fun getLayoutView(): View {
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        return mainBinding.root
    }

    override fun initPresenter() {
        presenter = MainPresenter(context = this, view = this)
    }

    override fun initViews() {
        matchRecordAdapter = MatchRecordAdapter(this)
        mainBinding.matchListRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.matchListRecyclerView.adapter = matchRecordAdapter
    }

    override fun initEventListener() {
        // 검색 EditText OnEditorAction
        mainBinding.userNameEditText.setOnEditorActionListener { _, action, _ ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                mainBinding.saerchButton.performClick()
                handled = true
            }
            handled
        }
        // 검색 Button OnClick
        mainBinding.saerchButton.setOnClickListener {
            it.hideSoftKeyboard() // 키보드 내리기

            if (mainBinding.userNameEditText.text.isNotEmpty()) {
                userName = mainBinding.userNameEditText.text.toString()

                matchList.clear()
                presenter.reqSearchSummoner(mainBinding.userNameEditText.text.toString())
            }
        }
    }

    override fun getSearchUserName(): String {
        return userName.trim().toLowerCase()
    }

    override fun replaceMatchRecordList(data: MatchRequiredDTO) {
        matchRequiredList.add(data)
        matchRecordAdapter.replaceList(matchRequiredList)
    }

    /**
     * 유저 프로필
     */
    override fun userProfile(summonerData: SummonerDTO) {
        // 유저 프로필 아이콘
        summonerData.profileIconId.let { iconId ->
            val profileIconImageUrl =
                "${IntroActivity.getCurrentVersionUrl()}${PROFILE_ICON_URL}${iconId}.png"
            setImageViewToGlide(mainBinding.userIconImageView, profileIconImageUrl)
        }
        mainBinding.userLevelTextView.text = summonerData.summonerLevel.toString()  // 레벨
        mainBinding.userNameTextView.text = summonerData.name                       // 유저명

    }

    /**
     * 유저 티어정보
     */
    override fun userRankInfo(userRankData: LeagueDTO) {
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
     * 유저 프로필 이미지
     */
    private fun userRankImageBinding(tier: String) {
        val imageResId = when (tier) {
            Tier.BRONZE.name -> R.drawable.emblem_bronze
            Tier.SILVER.name -> R.drawable.emblem_silver
            Tier.GOLD.name -> R.drawable.emblem_gold
            Tier.PLATINUM.name -> R.drawable.emblem_platinum
            Tier.DIAMOND.name -> R.drawable.emblem_diamond
            Tier.MASTER.name -> R.drawable.emblem_master
            Tier.GRANDMASTER.name -> R.drawable.emblem_grandmaster
            Tier.CHALLENGER.name -> R.drawable.emblem_challenger
            else -> R.drawable.ic_launcher_background  // TODO Unranked Image 필요
        }
        setImageViewToGlide(mainBinding.rankImageView, imageResId)
    }

    /**
     * ProgressBar Show / Hide
     */
    override fun showProgressBar(isVisible: Boolean) {
        mainBinding.progressBar.isVisible = isVisible
    }
}