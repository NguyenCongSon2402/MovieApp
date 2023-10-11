package com.oceantech.tracking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netflixclone.constants.BASE_IMG
import com.oceantech.tracking.data.models.home.Items
import com.oceantech.tracking.databinding.BottomSheetMediaDetailsBinding
import com.oceantech.tracking.ui.MovieDetailsActivity
import com.oceantech.tracking.ui.TvDetailsActivity

class MediaDetailsBottomSheet(private val items: Items) : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetMediaDetailsBinding

    //private val homeViewModel: HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BottomSheetMediaDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        updateUI()
    }

    private fun setupUI() {
        binding.closeIcon.setOnClickListener { dismiss() }
        binding.detailsButton.setOnClickListener {
            if (items.type == "single") {
                val intent = Intent(activity, MovieDetailsActivity::class.java)
                intent.putExtra("name", items.slug)
                startActivity(intent)
                dismiss()
            }else{
                val intent = Intent(activity, TvDetailsActivity::class.java)
                intent.putExtra("name", items.slug)
                startActivity(intent)
                dismiss()
            }
        }
    }

    private fun updateUI() {
        Glide.with(this).load(BASE_IMG + items.thumbUrl).transform(CenterCrop(), RoundedCorners(8))
            .into(binding.posterImage)
        binding.titleText.text = items.name
        binding.yearText.text = items.year.toString()
        binding.runtimeText.text = "  \u2022 " + items.time
        val categoryNames = items.category.joinToString("-") { it.name ?: "" }
        binding.overviewText.text = categoryNames
    }

    companion object {
        fun newInstance(items: Items): MediaDetailsBottomSheet {
            return MediaDetailsBottomSheet(items)
        }
    }
}