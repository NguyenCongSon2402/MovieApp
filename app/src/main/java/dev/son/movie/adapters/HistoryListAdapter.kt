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
import dev.son.movie.databinding.ItemPosterBinding
import dev.son.movie.network.models.home.Items
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.ViewingHistory

class HistoryListAdapter(private val onItemClick: (ViewingHistory, View) -> Unit) :
    ListAdapter<ViewingHistory, ItemHistoryViewHolder>(ItemHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHistoryViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHistoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ItemHistoryViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class ItemHistoryViewHolder(
    private val binding: ItemPosterBinding, private val onItemClick: ((ViewingHistory, View) -> Unit)
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(items: ViewingHistory?) {
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

class ItemHistoryDiffCallback : DiffUtil.ItemCallback<ViewingHistory>() {
    override fun areItemsTheSame(oldItem: ViewingHistory, newItem: ViewingHistory): Boolean {
        return oldItem.movieId == newItem.movieId
    }

    override fun areContentsTheSame(oldItem: ViewingHistory, newItem: ViewingHistory): Boolean {
        return oldItem == newItem
    }
}
