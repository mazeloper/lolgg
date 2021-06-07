package com.jschoi.develop.opgg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jschoi.develop.opgg.databinding.ItemMatchRecordBinding
import com.jschoi.develop.opgg.dto.MatchReferenceDTO

class MatchRecordAdapter : RecyclerView.Adapter<MatchRecordAdapter.ViewHolder>() {
    //데이터들을 저장하는 변수
    private var matchData = mutableListOf<MatchReferenceDTO>()

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

    fun replaceList(newList: MutableList<MatchReferenceDTO>) {
        matchData = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMatchRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchReferenceDTO) {
            binding.winOrLoseTextView.text = "${item.gameId} // ${item.champion}"
        }
    }
}