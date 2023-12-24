package dev.son.movie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop

import dev.son.movie.databinding.ItemMediaBinding
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.utils.setSingleClickListener

class MoviesAdapter(private val onItemClick: (Movie,View) -> Unit) :
        ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

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
        private val onItemClick:(Movie,View) -> Unit
) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie?) {
        if (movie == null) {
            return
        }
        Glide.with(binding.posterImage).load(movie.posterHorizontal).transform(CenterCrop())
                .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setSingleClickListener { onItemClick(movie,binding.posterImage) }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}