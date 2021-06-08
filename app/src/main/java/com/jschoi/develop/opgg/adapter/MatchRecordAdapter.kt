package com.jschoi.develop.opgg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jschoi.develop.opgg.Config.CHAMPION_INFO_URL
import com.jschoi.develop.opgg.Config.SPELL_IMAGE_URL
import com.jschoi.develop.opgg.databinding.ItemMatchRecordBinding
import com.jschoi.develop.opgg.dto.MatchDTO
import com.jschoi.develop.opgg.util.LogUtil
import com.jschoi.develop.opgg.view.activity.IntroActivity
import com.jschoi.develop.opgg.view.activity.MainActivity
import org.json.JSONObject

class MatchRecordAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<MatchRecordAdapter.ViewHolder>() {
    //데이터들을 저장하는 변수
    private var matchData = mutableListOf<MatchDTO>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
            LogUtil.warning(">>>>>>>>>>>>>>>${item.participantIdentities.toString()}")
            setCurrentSearchUserDataIndex(item) // 블루,레드팀 확인
            setCurrentWinOrLose(item)           // 승패확인

            // 챔피언 이미지
            val id = item.participants[searchUserIndex].championId.toString()
            val championInfo = IntroActivity.championList[id] as JSONObject
            val championImage = championInfo["image"] as JSONObject
            setImageViewToGlide(
                binding.championImageView,
                "$CHAMPION_INFO_URL${championImage["full"]}"
            )

            // 스펠 이미지
            val spell1Id = item.participants[searchUserIndex].spell1Id.toString()
            val spell2Id = item.participants[searchUserIndex].spell2Id.toString()
            setImageViewToGlide(
                binding.firstSpellImageView,
                "$SPELL_IMAGE_URL${IntroActivity.spellList[spell1Id]?.get("id")}.png"
            )
            setImageViewToGlide(
                binding.secondSpellImageView,
                "$SPELL_IMAGE_URL${IntroActivity.spellList[spell2Id]?.get("id")}.png"
            )
        }

        /**
         * 검색된 유저 - 참여자 목록에 인덱스 확인
         */
        private fun setCurrentSearchUserDataIndex(item: MatchDTO) {
            item.participantIdentities.forEachIndexed { index, data ->
                if (activity.getSearchUserName().trim() == ((data.player.summonerName).toLowerCase()
                        .trim())
                ) {
                    searchUserIndex = index
                    return@forEachIndexed
                }
            }
        }

        /**
         * 승패 확인
         */
        private fun setCurrentWinOrLose(item: MatchDTO) {
            binding.winOrLoseTextView.text =
                when (item.participants[searchUserIndex].stats.win) {
                    true -> {
                        "승리"
                    }
                    false -> {
                        "패배"
                    }
                }
        }

        /**
         * ImageView Glide 적용
         */
        private fun <T> setImageViewToGlide(view: ImageView, image: T) {
            Glide.with(activity)
                .load(image)
                .centerCrop()
                .into(view);
        }

    }
}