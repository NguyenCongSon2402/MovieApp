package com.oceantech.tracking.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.adapters.HomeItemsController
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentFeedBinding
import kotlin.math.min


class FeedFragment : TrackingBaseFragment<FragmentFeedBinding>() {

//    private lateinit var viewModel: MediaViewModel
//    private lateinit var feedViewModel: FeedViewModel
    private lateinit var homeItemsController: HomeItemsController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
        //(requireActivity() as BottomNavActivity).onFeedFragmentViewCreated()
    }

    override fun onFirstDisplay() {
//        feedViewModel.getFeedPagedList()
//            .observe(viewLifecycleOwner) { feedItemsController.submitList(it) }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(inflater, container, false)
    }

    private fun handleSearchClick() {
//        val intent = Intent(requireActivity(), SearchActivity::class.java)
//        startActivity(intent)
    }

    private fun handleMediaClick(media: Media) {
//        var id: Int? = null
//        if (media is Media.Movie) {
//            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
//                .show(requireActivity().supportFragmentManager, id.toString())
//        } else if (media is Media.Tv) {
//            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
//                .show(requireActivity().supportFragmentManager, id.toString())
//        }
    }

    private fun setupUI() {
        calculateAndSetListTopPadding()
        views.searchIcon.setOnClickListener { handleSearchClick() }

        views.feedItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollY: Int = views.feedItemsList.computeVerticalScrollOffset()
                val color = changeAlpha(
                    ContextCompat.getColor(requireActivity(), R.color.black_transparent),
                    (min(255, scrollY).toFloat() / 255.0f).toDouble()
                )
                views.appBarLayout.setBackgroundColor(color)
            }
        })


        homeItemsController = HomeItemsController()
        views.feedItemsList.adapter = homeItemsController.adapter
        homeItemsController.requestModelBuild()

        views.tvShowsTv.setOnClickListener {
//            val intent = Intent(requireActivity(), PopularTvActivity::class.java)
//            startActivity(intent)
        }

        views.moviesTv.setOnClickListener {
//            val intent = Intent(requireActivity(), PopularMoviesActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun setupViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            Injection.provideMediaViewModelFactory()
//        ).get(MediaViewModel::class.java)
//        feedViewModel = ViewModelProvider(
//            this,
//            Injection.provideFeedViewModelFactory()
//        ).get(FeedViewModel::class.java)
    }

    private fun calculateAndSetListTopPadding() {
        views.appBarLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val availableHeight: Int = views.appBarLayout.measuredHeight
                if (availableHeight > 0) {
                    views.appBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    //save height here and do whatever you want with it

                    val params = views.feedItemsList.layoutParams as ViewGroup.MarginLayoutParams
                    params.setMargins(0, -availableHeight, 0, 0)
                    views.feedItemsList.layoutParams = params
                }
            }
        })
    }

    private fun changeAlpha(color: Int, fraction: Double): Int {
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        val alpha: Int = (Color.alpha(color) * (fraction)).toInt()
        return Color.argb(alpha, red, green, blue)
    }
}

