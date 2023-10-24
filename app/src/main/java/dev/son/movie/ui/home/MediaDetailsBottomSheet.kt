package dev.son.movie.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.network.models.home.Items
import dev.son.movie.databinding.BottomSheetMediaDetailsBinding
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.TvDetailsActivity

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
            val categoryList = items.category
            val shuffledIndices = categoryList.indices.shuffled()
            val randomIndex = shuffledIndices.first() // Lấy chỉ mục đầu tiên từ danh sách đã xáo trộn
            val randomCategory = categoryList[randomIndex]
            val randomSlug = randomCategory.slug

            if (items.type == "single") {
                val intent = Intent(activity, MovieDetailsActivity::class.java)
                intent.putExtra("name", items.slug)
                intent.putExtra("category", randomSlug)
                startActivity(intent)
                dismiss()
            }else{
                val intent = Intent(activity, TvDetailsActivity::class.java)
                intent.putExtra("name", items.slug)
                intent.putExtra("category", randomSlug)
                intent.putExtra("thumbUrl",items.thumbUrl)
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