package dev.son.movie.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.databinding.ItemMediaBinding
import dev.son.movie.network.models.home.Items

class SearchMovieAdapter(private val onItemClick: (Items) -> Unit) :
    ListAdapter<Items, MovieItemViewHolder>(MediaItemDiffCallback()) {

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
    private val onItemClick: ((Items) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: Items?) {
        if (items == null) {
            return
        }

        var posterUrl: String? = null
        posterUrl = BASE_IMG + items.thumbUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(items) }
    }
}

class MediaItemDiffCallback : DiffUtil.ItemCallback<Items>() {
    override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem == newItem
    }
}