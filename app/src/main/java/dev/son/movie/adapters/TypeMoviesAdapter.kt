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
import dev.son.movie.databinding.ItemMovieBinding
import dev.son.movie.network.models.home.Items
import dev.son.movie.utils.setSingleClickListener

class TypeMoviesAdapter(private val onItemClick: (Items, View) -> Unit) :
    ListAdapter<Items, TypeMovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeMovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TypeMovieViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: TypeMovieViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class TypeMovieViewHolder(
    var binding: ItemMovieBinding,
    private val onItemClick: (Items, View) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Items?) {
        if (movie == null) {
            return
        }
        var posterUrl: String? = null
        posterUrl = BASE_IMG + movie.thumbUrl
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true
        binding.nameText.text = movie.name
        itemView.setSingleClickListener { onItemClick(movie, itemView) }
    }
}
