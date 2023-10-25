package dev.son.movie.adapters

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.son.movie.databinding.ItemPickerOptionBinding
import dev.son.movie.network.models.countries.Items

class CountriesMovieAdapter(private val onItemClick: (Items) -> Unit) :
    ListAdapter<Items, PickerItemViewHolder>(PickerItemDiffCallback()) {

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
    private val onItemClick: (Items) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(pickerItem: Items) {
        binding.optionText.text = pickerItem.name
        binding.root.setOnClickListener { onItemClick(pickerItem) }
    }
}

class PickerItemDiffCallback : DiffUtil.ItemCallback<Items>() {
    override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
        return oldItem == newItem
    }
}