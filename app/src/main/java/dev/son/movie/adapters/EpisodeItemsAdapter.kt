package dev.son.movie.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.data.models.Slug.ServerData
import dev.son.movie.databinding.ItemEpisodeBinding
import dev.son.movie.utils.hide

class EpisodeItemsAdapter(private val onItemClick: (ServerData) -> Unit,private  val imgPosterUrl: String) :
    ListAdapter<ServerData, EpisodeItemViewHolder>(EpisodeItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeItemViewHolder(binding, onItemClick,imgPosterUrl)
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode, position)
    }
}

class EpisodeItemViewHolder(
    private val binding: ItemEpisodeBinding,
    private val onItemClick: ((ServerData) -> Unit),
    private val imgPosterUrl: String
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(episode: ServerData, position: Int) {
        val title = "${episode.filename}"
        val stillUrl = episode.slug
        Glide.with(binding.stillImage).load(BASE_IMG+imgPosterUrl).transform(CenterCrop())
            .into(binding.stillImage)
        binding.stillImage.clipToOutline = true
        binding.nameText.text = title
        binding.ratingText.hide()
        binding.overviewText.hide()
        binding.root.setOnClickListener { onItemClick(episode) }
    }
}

class EpisodeItemDiffCallback : DiffUtil.ItemCallback<ServerData>() {
    override fun areItemsTheSame(oldItem: ServerData, newItem: ServerData): Boolean {
        return oldItem.name == newItem.name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ServerData, newItem: ServerData): Boolean {
        return oldItem == newItem
    }
}