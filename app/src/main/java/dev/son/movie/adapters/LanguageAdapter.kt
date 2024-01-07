package dev.son.movie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.son.movie.R
import dev.son.movie.databinding.ItemLanguageBinding
import dev.son.movie.language.Language

class LanguageAdapter(
    private val context: Context,
    private val listener: OnLanguageClickListener
) : ListAdapter<Language, LanguageAdapter.LanguageViewHolder>(LanguageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(inflater, parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Language) {
            binding.txtNameLanguage.text = item.title
            binding.imgIconLanguage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    item.flag
                )
            )

            if (item.isChoose) {
                binding.imgChooseLanguage.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_select_language)
                )
            } else {
                binding.imgChooseLanguage.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_un_select_lang)
                )
            }

            binding.root.setOnClickListener {
                listener.onClickItemListener(item)

                for (i in currentList.indices) {
                    currentList[i].isChoose = i == currentList.indexOf(item)
                }
                notifyDataSetChanged()
            }

            if (adapterPosition == currentList.size - 1) {
                binding.root.setBackgroundResource(R.drawable.bg_border_gray_10sdp)
            }
        }
    }

    interface OnLanguageClickListener {
        fun onClickItemListener(language: Language?)
    }

    private class LanguageDiffCallback : DiffUtil.ItemCallback<Language>() {
        override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem == newItem
        }
    }
}
