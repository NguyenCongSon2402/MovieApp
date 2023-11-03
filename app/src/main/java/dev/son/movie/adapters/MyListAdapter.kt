package dev.son.movie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.databinding.ItemPosterBinding
import dev.son.movie.network.models.user.MovieId1

class MyListAdapter(private val onItemClick: (MovieId1, View) -> Unit) :
    ListAdapter<MovieId1, ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class ItemViewHolder(
    var binding: ItemPosterBinding,
    private val onItemClick: ((MovieId1, View) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: MovieId1?) {
        if (items == null) {
            return
        }

        var posterUrl: String? = null
        posterUrl = BASE_IMG + items.thumbUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(items, itemView) }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<MovieId1>() {
    override fun areItemsTheSame(oldItem: MovieId1, newItem: MovieId1): Boolean {
        return oldItem.movieId1 == newItem.movieId1
    }

    override fun areContentsTheSame(oldItem: MovieId1, newItem: MovieId1): Boolean {
        return oldItem == newItem
    }
}