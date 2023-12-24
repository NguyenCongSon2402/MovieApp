package dev.son.movie.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.son.movie.databinding.ItemCategoryBinding
import dev.son.movie.network.models.movie.Genre

class CategoryAdapter(private val onItemClick: (Genre,Boolean) -> Unit) :
    ListAdapter<Genre, CheckItemViewHolder>(CheckItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckItemViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CheckItemViewHolder, position: Int) {
        val pickerItem = getItem(position)
        holder.bind(pickerItem)
    }
}

class CheckItemViewHolder(
    private val binding: ItemCategoryBinding,
    private val onItemClick: (Genre,Boolean) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(pickerItem: Genre) {
        binding.checkbox.text = pickerItem.name
        binding.checkbox.isChecked=pickerItem.selected
        if (binding.checkbox.isChecked){
            onItemClick(pickerItem,true)
        }
        binding.checkbox.setOnCheckedChangeListener { buttonCheck, isChecked ->
            onItemClick(pickerItem,isChecked)
        }
    }
}

class CheckItemDiffCallback : DiffUtil.ItemCallback<Genre>() {
    override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem == newItem
    }
}