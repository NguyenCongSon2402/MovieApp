package com.oceantech.tracking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.BASE_IMG
import com.oceantech.tracking.data.models.Slug.Item
import com.oceantech.tracking.data.models.home.Items

import com.oceantech.tracking.databinding.ItemMediaBinding
import com.oceantech.tracking.utils.setSingleClickListener

class MoviesAdapter(private val onItemClick: (Items,View) -> Unit) :
        ListAdapter<Items, MovieViewHolder>(MovieDiffCallback()) {

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
        private val onItemClick:(Items,View) -> Unit
) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Items?) {
        if (movie == null) {
            return
        }
        var posterUrl: String? = null
        posterUrl = BASE_IMG + movie.thumbUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
                .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setSingleClickListener { onItemClick(movie,binding.posterImage) }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Items>() {
    override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem == newItem
    }
}