package dev.son.movie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.son.movie.R
import dev.son.movie.databinding.ItemTextfeedbackBinding
import dev.son.movie.network.models.FeedBack

class FeedBackAdapter(
    private val context: Context,
    private val onItemClicked: OnItemClicked
) : ListAdapter<FeedBack, FeedBackAdapter.FeedBackViewHolder>(FeedBackDiffCallback()) {

    interface OnItemClicked {
        fun onItemClicked(position: Int, feedBack: FeedBack, isSelectedOne: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedBackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTextfeedbackBinding.inflate(inflater, parent, false)
        return FeedBackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedBackViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class FeedBackViewHolder(private val binding: ItemTextfeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FeedBack, position: Int) {
            binding.tvItem.text = item.text

            binding.root.setOnClickListener {
                item.isSelected = !item.isSelected

                var isHaveItemSelected = false
                for (checkItem in currentList) {
                    if (checkItem.isSelected) {
                        isHaveItemSelected = true
                        break
                    }
                }

                onItemClicked.onItemClicked(position, item, isHaveItemSelected)
                notifyItemChanged(position)

                if (item.isSelected) {
                    binding.layout.setBackgroundResource(R.drawable.bg_textfeedback)
                } else {
                    binding.layout.setBackgroundResource(R.drawable.bg_textfeedbacktrue)
                }
            }

            if (item.isSelected) {
                binding.layout.setBackgroundResource(R.drawable.bg_textfeedback)
            } else {
                binding.layout.setBackgroundResource(R.drawable.bg_textfeedbacktrue)
            }
        }
    }
}

class FeedBackDiffCallback : DiffUtil.ItemCallback<FeedBack>() {
    override fun areItemsTheSame(oldItem: FeedBack, newItem: FeedBack): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: FeedBack, newItem: FeedBack): Boolean {
        return oldItem == newItem
    }
}
