package com.jschoi.develop.opgg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jschoi.develop.opgg.Config.CHAMPION_INFO_URL
import com.jschoi.develop.opgg.databinding.ItemMatchRecordBinding
import com.jschoi.develop.opgg.dto.MatchDTO
import com.jschoi.develop.opgg.view.activity.IntroActivity
import com.jschoi.develop.opgg.view.activity.MainActivity
import org.json.JSONObject

class MatchRecordAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<MatchRecordAdapter.ViewHolder>() {
    //데이터들을 저장하는 변수
    private var matchData = mutableListOf<MatchDTO>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // TODO("Not yet implemented")
        return ViewHolder(
            ItemMatchRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(matchData[position])
    }

    override fun getItemCount(): Int {
        // TODO("Not yet implemented")
        return matchData.size
    }

    fun replaceList(newList: MutableList<MatchDTO>) {
        matchData = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMatchRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var searchUserIndex = 0

        fun bind(item: MatchDTO) {
            getCurrentSearchUserDataIndex(item)
            getCurrentWinOrLose(item)   // 승패확인

            val id = item.participants.get(searchUserIndex).championId.toString()
            val championInfo = IntroActivity.championList[id] as JSONObject
            val championImage = championInfo["image"] as JSONObject

            Glide.with(activity.applicationContext)
                .load("$CHAMPION_INFO_URL${championImage["sprite"]}")
                .into(binding.championImageView)

        }

        private fun getCurrentSearchUserDataIndex(item: MatchDTO) {
            item.participantIdentities.forEachIndexed { index, data ->
                if (activity.getSearchUserName() == (data.player.summonerName).toLowerCase()) {
                    searchUserIndex = index
                    return@forEachIndexed
                }
            }
        }

        // 승패 확인로직
        private fun getCurrentWinOrLose(item: MatchDTO) {
            val teamId = searchUserIndex / 5

            binding.winOrLoseTextView.run {
                binding.queueTypeTextView.text = item.teams[teamId].win.toString()
                when (item.teams[teamId].win) {
                    "Win" -> {
                        this.text = "승리"
                    }
                    else -> {
                        this.text = "패배"
                    }
                }
            }
        }
    }
}