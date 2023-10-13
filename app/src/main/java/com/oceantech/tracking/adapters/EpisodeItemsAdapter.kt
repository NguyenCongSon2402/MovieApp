package com.oceantech.tracking.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.oceantech.tracking.data.models.Slug.ServerData
import com.oceantech.tracking.databinding.ItemEpisodeBinding
import com.oceantech.tracking.utils.hide

class EpisodeItemsAdapter(private val onItemClick: ((ServerData) -> Unit)) :
    ListAdapter<ServerData, EpisodeItemViewHolder>(EpisodeItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode, position)
    }
}

class EpisodeItemViewHolder(
    private val binding: ItemEpisodeBinding,
    private val onItemClick: ((ServerData) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(episode: ServerData, position: Int) {
        val title = "${position + 1}. ${episode.name}"
        val stillUrl = episode.slug
        Glide.with(binding.stillImage).load(stillUrl).transform(CenterCrop())
            .into(binding.stillImage)
        binding.stillImage.clipToOutline = true
        binding.nameText.text = title
        binding.ratingText.hide()
        binding.overviewText.hide()
        binding.root.setOnClickListener { onItemClick(episode) }
    }
}

class EpisodeItemDiffCallback : DiffUtil.ItemCallback<ServerData>() {
    override fun areItemsTheSame(oldItem: ServerData, newItem: ServerData): Boolean {
        return oldItem.name == newItem.name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ServerData, newItem: ServerData): Boolean {
        return oldItem == newItem
    }
}