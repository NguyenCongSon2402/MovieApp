package dev.son.movie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.databinding.ItemMediaBinding
import dev.son.movie.databinding.ItemPosterBinding
import dev.son.movie.network.models.home.Items
import dev.son.movie.network.models.user.MovieId1

class FavoriteListAdapter(private val onItemClick: (MovieId1, View) -> Unit) :
    ListAdapter<MovieId1, ItemFavoriteViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFavoriteViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemFavoriteViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ItemFavoriteViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class ItemFavoriteViewHolder(
    var binding: ItemPosterBinding,
    private val onItemClick: ((MovieId1, View) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(items: MovieId1?) {
        if (items == null) {
            return
        }

        var posterUrl: String?
        posterUrl = BASE_IMG + items.thumbUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(items, itemView) }
    }
}
