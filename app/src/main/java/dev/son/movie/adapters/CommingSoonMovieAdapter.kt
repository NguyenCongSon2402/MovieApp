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
import dev.son.movie.databinding.ItemUpcomingMovieBinding
import dev.son.movie.network.models.home.Items

class CommingSoonMovieAdapter(private val onItemClick: (Items, View) -> Unit) :
    ListAdapter<Items, MovieItemsViewHolder>(MovieItemDiffCallback()) {

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
    private val onItemClick: ((Items,View) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: Items?) {
        if (items == null) {
            return
        }

        var posterUrl: String? = null
        posterUrl = BASE_IMG + items.thumbUrl
        Glide.with(binding.backdropImage).load(posterUrl).transform(CenterCrop())
            .into(binding.backdropImage)
        binding.backdropImage.clipToOutline = true
        binding.titleText.text = items.name
        binding.arrivalDateText.text = items.year.toString()
        binding.hdText.text = items.quality
        binding.runtimeText.text = items.time
        binding.langText.text = items.lang
        binding.episodeCurrentText.text = items.episodeCurrent
        val categories = items.category

        val categoryNames = categories?.joinToString("-") { it.name.toString() }

        binding.genresText.text = categoryNames ?: "No categories available"

        itemView.setOnClickListener { onItemClick(items,itemView) }
    }
}

class MovieItemDiffCallback : DiffUtil.ItemCallback<Items>() {
    override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem == newItem
    }
}