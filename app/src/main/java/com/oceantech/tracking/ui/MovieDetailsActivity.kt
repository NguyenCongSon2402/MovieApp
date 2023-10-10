package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.adapters.MoviesAdapter
import com.oceantech.tracking.adapters.VideosController
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.models.Slug.Item
import com.oceantech.tracking.data.models.Slug.Slug
import com.oceantech.tracking.data.models.home.Items
import com.oceantech.tracking.databinding.ActivityMovieDetailsBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.MediaDetailsBottomSheet
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.extractVideoIdFromUrl
import com.oceantech.tracking.utils.hide
import com.oceantech.tracking.utils.show
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

class MovieDetailsActivity : TrackingBaseActivity<ActivityMovieDetailsBinding>(),
    HomeViewModel.Factory {
    private val homeViewModel: HomeViewModel by viewModel()

    //private lateinit var similarMoviesItemsAdapter: MoviesAdapter
    private lateinit var videosController: VideosController
    private val movieSlug: String?
        get() = intent.extras?.getString("name")

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        movieSlug?.let { homeViewModel.handle(HomeViewAction.getSlug(name = it)) }
        setupUI()
        homeViewModel.subscribe(this) {
            when (it.slug) {
                is Success -> {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
                    homeViewModel.handleRemoveStateSlug()
                    showLoader(false)
                    updateDetails(it.slug.invoke())
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.slug)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateSlug()
                    showLoader(true)
                }

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
        views.tabLayout.removeOnTabSelectedListener(tabSelectedListener)
    }

    override fun getBinding(): ActivityMovieDetailsBinding {
        return ActivityMovieDetailsBinding.inflate(layoutInflater)
    }

    private fun handleMovieClick(item: Items) {
        MediaDetailsBottomSheet.newInstance(item)
            .show(supportFragmentManager, item.Id.toString())
    }

    private fun setupUI() {
        views.toolbar.setNavigationOnClickListener { finish() }
        views.loader.root.show()
        views.content.hide()
        views.youtubePlayerView.hide()
        views.thumbnail.container.hide()
        views.thumbnail.playContainer.setOnClickListener { replayVideo() }

        views.header.overviewText.setOnClickListener {
            views.header.overviewText.maxLines = 10
            views.header.overviewText.isClickable = false
        }
        views.header.playLl.setOnClickListener {

        }

        views.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        views.tabLayout.addOnTabSelectedListener(tabSelectedListener)

//        similarMoviesItemsAdapter = MoviesAdapter(this::handleMovieClick)
//        binding.similarMoviesList.adapter = similarMoviesItemsAdapter
//        binding.similarMoviesList.isNestedScrollingEnabled = false
//
//        videosController = VideosController {}
//        views.videosList.adapter = videosController.adapter
//        views.videosList.isNestedScrollingEnabled = false
    }

    private fun showLoader(flag: Boolean) {
        if (flag) {
            views.loader.root.show()
            views.content.hide()
            views.youtubePlayerView.hide()
            views.thumbnail.container.hide()
        } else {
            views.loader.root.hide()
            views.content.show()
            views.thumbnail.container.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDetails(data: Slug) {
        // Basic details
        Glide.with(this).load(data.data?.seoOnPage?.seoSchema?.image).transform(CenterCrop())
            .into(views.thumbnail.backdropImage)
        views.header.titleText.text = data.data?.item?.name
        views.header.overviewText.text = data.data?.item?.content
        views.header.yearText.text = data.data?.item?.year.toString()
        views.header.runtimeText.text = data.data?.item?.time
        views.header.ratingText.text = data.data?.item?.lang

//        // Similar movies
//        similarMoviesItemsAdapter.submitList(details.similar.results)
//        similarMoviesItemsAdapter.notifyDataSetChanged()

        // Videos
        checkAndLoadVideo(data.data?.item!!)
        //videosController.setData(details.videos.results)
    }

    private fun checkAndLoadVideo(videos: Item) {
        val trailerUrl = videos.trailerUrl
        if (!trailerUrl.isNullOrBlank()) {
            val videoId = extractVideoIdFromUrl(trailerUrl)
            if (videoId != null && !bannerVideoLoaded) {
                views.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        player = youTubePlayer
                        youTubePlayer.loadVideo(videoId, 0f)
                        bannerVideoLoaded = true
                    }
                })
            }
        } else {
            views.thumbnail.playContainer.hide()
        }
    }

    private fun replayVideo() {
        if (player != null) {
            player!!.seekTo(0f)
            lifecycleScope.launch {
                delay(500)
                views.youtubePlayerView.show()
                views.thumbnail.container.hide()
            }
        }
    }

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            if (!isVideoRestarted && second > 3.2) {
                isVideoRestarted = true
                lifecycleScope.launch {
                    youTubePlayer.seekTo(0f)
                    youTubePlayer.unMute()
                    //views.youtubePlayerView.getPlayerUiController().showUi(false)
                    delay(50)
                    views.thumbnail.container.hide()
                    views.thumbnail.videoLoader.hide()
                    views.youtubePlayerView.show()
                    delay(1000)
                    //views.youtubePlayerView.getPlayerUiController().showUi(true)
                }
            }
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState,
        ) {
            if ((state == PlayerConstants.PlayerState.UNSTARTED) && !isVideoRestarted) {
                youTubePlayer.mute()
            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                views.youtubePlayerView.hide()
                views.thumbnail.container.show()
                views.thumbnail.videoLoader.hide()
            }
        }
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab?.position == 0) {
                views.similarMoviesList.show()
                views.videosList.hide()
            } else {
                views.similarMoviesList.hide()
                views.videosList.show()
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

}