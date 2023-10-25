package dev.son.movie.ui.home

import android.content.Intent
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.son.movie.R
import dev.son.movie.adapters.CountriesMovieAdapter
import dev.son.movie.databinding.FragmentItemPickerBinding
import dev.son.movie.network.models.countries.Data
import dev.son.movie.network.models.countries.Items
import dev.son.movie.ui.CategoryMoviesActivity
import dev.son.movie.ui.MovieDetailsActivity

class ItemPickerFragment(private val data: Data, private val title: String) : DialogFragment() {
    private lateinit var binding: FragmentItemPickerBinding
    private lateinit var pickerItemsAdapter: CountriesMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemPickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }


    private fun handleItemClick(item: Items) {
        val intent = Intent(activity, CategoryMoviesActivity::class.java)
        intent.putExtra("slug", item.slug)
        intent.putExtra("name", item.name)
        intent.putExtra("title", title)
        startActivity(intent)
        dismiss()
    }

    private fun setupUI() {
        pickerItemsAdapter = CountriesMovieAdapter(this::handleItemClick)
        binding.optionsList.adapter = pickerItemsAdapter
        pickerItemsAdapter.submitList(data.items)
        pickerItemsAdapter.notifyDataSetChanged()
        binding.title.text = title
        binding.content.setOnClickListener { dismiss() }
        binding.closeIcon.setOnClickListener { dismiss() }
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    companion object {
        fun newInstance(data: Data, s: String): ItemPickerFragment {
            return ItemPickerFragment(data, s)
        }
    }
}