package dev.son.movie.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.son.movie.databinding.ItemEpisodeBinding

class EpisodeItemsAdapter(private val onItemClick: (String, Int) -> Unit) :
    ListAdapter<String, EpisodeItemViewHolder>(EpisodeItemDiffCallback()) {
    private var selectedItem = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val episode = getItem(position)
        holder.bind(episode, position)
        holder.itemView.setOnClickListener {
            val previousItem = selectedItem
            selectedItem = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            onItemClick(episode, position)
        }
        holder.itemView.isSelected = selectedItem == position
    }

}

class EpisodeItemViewHolder(
    private val binding: ItemEpisodeBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(episode: String, position: Int) {
        binding.txtEpisode.text = (position+1).toString()
    }
}

class EpisodeItemDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}