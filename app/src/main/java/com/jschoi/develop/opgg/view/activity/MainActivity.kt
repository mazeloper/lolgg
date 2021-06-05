package com.jschoi.develop.opgg.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jschoi.develop.opgg.R
import com.jschoi.develop.opgg.databinding.ActivityMainBinding
import com.jschoi.develop.opgg.dto.LeagueDTO
import com.jschoi.develop.opgg.dto.SummonerDTO
import com.jschoi.develop.opgg.enum.Tier
import com.jschoi.develop.opgg.network.RetrofitClient
import com.jschoi.develop.opgg.network.RetrofitService
import com.jschoi.develop.opgg.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var summonerService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initViews()
        initRetrofit()
    }

    private fun initViews() {
        mainBinding.saerchButton.setOnClickListener {
            if (mainBinding.userNameEditText.text.isNotEmpty()) {
                reqSearchSummoner()
            }
        }
    }


    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        summonerService = retrofit.create(RetrofitService::class.java)
    }

    /**
     * 유저 티어정보
     */
    private fun userRankInfo(userRankData: LeagueDTO) {
        // 티어이미지
        userRankImageBinding(userRankData.tier)
    }

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
                R.drawable.ic_launcher_background
            }
        }
        mainBinding.rankImageView.setImageResource(imageResId)
    }

    /**
     * API 통신 - UserID 검색
     */
    private fun reqSearchSummoner() {
        val userName = mainBinding.userNameEditText.text.toString()
        summonerService.reqSummonerId(userName, getString(R.string.riot_api_key))
            .enqueue(object : Callback<SummonerDTO> {
                override fun onResponse(call: Call<SummonerDTO>, response: Response<SummonerDTO>) {

                    if (response.isSuccessful.not()) return
                    // 유저 ID -> 리그 API 호출
                    response.body()?.let {
                        reqSummonerRankInfo(it)
                    }

                }

                override fun onFailure(call: Call<SummonerDTO>, t: Throwable) {
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }
            })
    }

    /**
     * API 통신 - UserID 매칭 랭크 정보
     */
    private fun reqSummonerRankInfo(summonerData: SummonerDTO) {
        val userId = summonerData.id
        summonerService.reqSummonerRankInfo(userId, getString(R.string.riot_api_key))
            .enqueue(object : Callback<List<LeagueDTO>> {
                override fun onResponse(
                    call: Call<List<LeagueDTO>>,
                    response: Response<List<LeagueDTO>>
                ) {
                    if (response.isSuccessful.not()) return
                    // 유저 ID -> 리그 API 호출
                    response.body()?.let { rankData ->
                        // Util.showDialogMessage(this@MainActivity, response.body().toString())
                        userRankInfo(rankData.first())
                    }
                }

                override fun onFailure(call: Call<List<LeagueDTO>>, t: Throwable) {
                    Util.showDialogMessage(this@MainActivity, t.toString())
                }

            })
    }
}