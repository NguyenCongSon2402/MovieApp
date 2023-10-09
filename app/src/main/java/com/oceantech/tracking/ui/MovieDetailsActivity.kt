package com.oceantech.tracking.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.models.Items
import com.oceantech.tracking.databinding.ActivityMovieDetailsBinding
import com.oceantech.tracking.ui.home.MediaDetailsBottomSheet
import com.oceantech.tracking.utils.hide
import com.oceantech.tracking.utils.show
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class  MovieDetailsActivity : TrackingBaseActivity<ActivityMovieDetailsBinding>() {
//    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()
//    private lateinit var similarMoviesItemsAdapter: MoviesAdapter
//    private lateinit var videosController: VideosController
    private val movieId: String?
        get() = intent.extras?.getString("name")

    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        // setupViewModel()
        fetchData()
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

//    @SuppressLint("NotifyDataSetChanged")
//    private fun setupViewModel() {
//        movieDetailsViewModel.details.observe(this) {
//            if (it.isLoading && it.data == null) {
//                showLoader(true)
//            } else if (it.data != null) {
//                showLoader(false)
//                updateDetails(it.data)
//            }
//        }
//    }

    private fun fetchData() {
        if (movieId != null) {
            //movieDetailsViewModel.fetchMovieDetails(movieId!!)
        }
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

//    @SuppressLint("NotifyDataSetChanged")
//    private fun updateDetails(details: MovieDetailsResponse) {
//        // Basic details
//        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
//            .into(views.thumbnail.backdropImage)
//        views.header.titleText.text = details.title
//        views.header.overviewText.text = details.overview
//        views.header.yearText.text = details.getReleaseYear()
//        views.header.runtimeText.text = details.getRunTime()
//        views.header.ratingText.text = details.voteAverage.toString()
//
//        // Similar movies
//        similarMoviesItemsAdapter.submitList(details.similar.results)
//        similarMoviesItemsAdapter.notifyDataSetChanged()
//
//        // Videos
//        checkAndLoadVideo(details.videos.results)
//        videosController.setData(details.videos.results)
//    }

//    private fun checkAndLoadVideo(videos: List<Video>) {
//        val firstVideo =
//            videos.firstOrNull { video -> (video.type == "Trailer") && video.site == "YouTube" }
//        if (firstVideo != null) {
//            if (!bannerVideoLoaded) {
//                views.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
//                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
//                        player = youTubePlayer
//                        youTubePlayer.loadVideo(firstVideo.key, 0f)
//                        bannerVideoLoaded = true
//                    }
//                })
//            }
//        } else {
//            views.thumbnail.playContainer.hide()
//        }
//    }

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
}