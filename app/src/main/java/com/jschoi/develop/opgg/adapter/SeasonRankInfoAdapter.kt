package com.jschoi.develop.opgg.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jschoi.develop.opgg.databinding.ItemSeasonRankInfoBinding

class SeasonRankInfoAdapter(context: Context) :
    RecyclerView.Adapter<SeasonRankInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSeasonRankInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(private val binding: ItemSeasonRankInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.seasonTitleTextView.text = "2020"
            binding.seasonRankTextView.text = "실버"
        }
    }
}