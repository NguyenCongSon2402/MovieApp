package dev.son.movie.adapters

import  android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dev.son.movie.databinding.ItemUpcomingMovieBinding
import dev.son.movie.network.models.movie.Movie

class CommingSoonMovieAdapter(private val onItemClick: (Movie, View) -> Unit) :
    ListAdapter<Movie, MovieItemsViewHolder>(MovieItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemsViewHolder {
        val binding =
            ItemUpcomingMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieItemsViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MovieItemsViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }
}

class MovieItemsViewHolder(
    var binding: ItemUpcomingMovieBinding,
    private val onItemClick: ((Movie, View) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: Movie?) {
        if (items == null) {
            return
        }

        var posterUrl: String? = null
        Glide.with(binding.backdropImage).load(items.posterHorizontal).transform(CenterCrop())
            .into(binding.backdropImage)
        binding.backdropImage.clipToOutline = true
        binding.titleText.text = items.title
        binding.arrivalDateText.text = items.releaseYear.toString()
        binding.runtimeText.text = items.duration
        binding.ratingText.text = items.rating?.toString() ?: "0"

        binding.textViewCountRates.text = items.numberOfReviews?.toString() ?: "0 "
//        val categories = items.category
//
//        val categoryNames = categories?.joinToString("-") { it.name.toString() }
//
//        binding.genresText.text = categoryNames ?: "No categories available"

        itemView.setOnClickListener { onItemClick(items, itemView) }
    }
}

class MovieItemDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}