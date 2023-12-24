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
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.user.MovieId1

class MyListAdapter(private val onItemClick: (Movie, View) -> Unit) :
    ListAdapter<Movie, ItemViewHolder>(ItemDiffCallback()) {

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
    private val onItemClick: ((Movie, View) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: Movie?) {
        if (items == null) {
            return
        }

        Glide.with(binding.posterImage).load(items.posterHorizontal).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(items, itemView) }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}