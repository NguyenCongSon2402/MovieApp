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
import dev.son.movie.databinding.ItemMediaBinding
import dev.son.movie.network.models.movie.Movie

class SearchMovieAdapter(private val onItemClick: (Movie, View) -> Unit) :
    ListAdapter<Movie, MovieItemViewHolder>(MediaItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class MovieItemViewHolder(
    var binding: ItemMediaBinding,
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

class MediaItemDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}