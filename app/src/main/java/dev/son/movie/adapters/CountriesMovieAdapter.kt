package dev.son.movie.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.son.movie.databinding.ItemPickerOptionBinding
import dev.son.movie.network.models.movie.Genre

class CountriesMovieAdapter(private val onItemClick: (Genre) -> Unit) :
    ListAdapter<Genre, PickerItemViewHolder>(PickerItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerItemViewHolder {
        val binding =
            ItemPickerOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickerItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PickerItemViewHolder, position: Int) {
        val pickerItem = getItem(position)
        holder.bind(pickerItem)
    }
}

class PickerItemViewHolder(
    private val binding: ItemPickerOptionBinding,
    private val onItemClick: (Genre) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(pickerItem: Genre) {
        binding.optionText.text = pickerItem.name
        binding.root.setOnClickListener { onItemClick(pickerItem) }
    }
}

class PickerItemDiffCallback : DiffUtil.ItemCallback<Genre>() {
    override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem == newItem
    }
}