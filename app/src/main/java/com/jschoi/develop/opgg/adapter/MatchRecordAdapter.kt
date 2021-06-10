package com.jschoi.develop.opgg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jschoi.develop.opgg.databinding.ItemMatchRecordBinding
import com.jschoi.develop.opgg.dto.MatchRequiredDTO
import com.jschoi.develop.opgg.view.activity.MainActivity

class MatchRecordAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<MatchRecordAdapter.ViewHolder>() {
    //데이터들을 저장하는 변수
    private var matchData = mutableListOf<MatchRequiredDTO>()


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

    fun replaceList(newList: MutableList<MatchRequiredDTO>) {
        matchData = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMatchRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchRequiredDTO) {
            // 승패
            binding.winOrLoseTextView.text = item.win
            // 챔피언 이미지
            activity.setImageViewToGlide(binding.championImageView, item.championImageUrl)
            // 스펠 이미지 1
            activity.setImageViewToGlide(binding.firstSpellImageView, item.spell1)
            // 스펠 이미지 2
            activity.setImageViewToGlide(binding.secondSpellImageView, item.spell2)
            // 룬 이미지 1
            activity.setImageViewToGlide(binding.firstRuneImageView, item.runes1)
            // 룬 이미지 2
            activity.setImageViewToGlide(binding.secondRuneImageView, item.runes2)
        }
    }
}