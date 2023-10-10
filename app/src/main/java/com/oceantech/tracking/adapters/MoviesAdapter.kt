package com.oceantech.tracking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.oceantech.tracking.data.models.Slug.Item

import com.oceantech.tracking.databinding.ItemMediaBinding

class MoviesAdapter(private val onItemClick: ((Item) -> Unit)) :
        ListAdapter<Item, MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class MovieViewHolder(
        var binding: ItemMediaBinding,
        private val onItemClick:(Item) -> Unit
) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Item?) {
        if (movie == null) {
            return
        }

        val posterUrl = movie.posterUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
                .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(movie) }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}