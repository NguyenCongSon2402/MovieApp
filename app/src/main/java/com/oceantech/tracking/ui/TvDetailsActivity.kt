package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.models.home.Items
import com.oceantech.tracking.databinding.ActivityTvDetailsScreenBinding
import com.oceantech.tracking.ui.home.MediaDetailsBottomSheet
import com.oceantech.tracking.utils.hide
import com.oceantech.tracking.utils.show
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TvDetailsActivity : TrackingBaseActivity<ActivityTvDetailsScreenBinding>() {
//    private val tvShowDetailsViewModel: TvShowDetailsViewModel by viewModels()
//    private lateinit var episodeItemsAdapter: EpisodeItemsAdapter
//    private lateinit var similarTvItemsAdapter: TvShowsAdapter
//    private lateinit var videosController: VideosController
    private val tvId: String?
        get() = intent.extras?.getString("id")


    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
//        setupViewModel()
//        fetchInitialData()
    }


    override fun onDestroy() {
        super.onDestroy()
        views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
    }

    private fun handleTvClick(item: Items) {
        MediaDetailsBottomSheet.newInstance(item)
            .show(supportFragmentManager, item.Id.toString())
    }

    private fun setupUI() {
        views.toolbar.setNavigationOnClickListener { finish() }
        title = ""
        showBackIcon()

        views.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        views.header.overviewText.setOnClickListener {
            views.header.overviewText.maxLines = 10
            views.header.overviewText.isClickable = false
        }
        views.menusTabLayout.addOnTabSelectedListener(tabSelectedListener)

//        views.seasonPicker.setOnClickListener { handleSeasonPickerSelectClick() }

//        episodeItemsAdapter = EpisodeItemsAdapter {}
//        binding.episodesList.adapter = episodeItemsAdapter
//        binding.episodesList.isNestedScrollingEnabled = false
//
//        similarTvItemsAdapter = TvShowsAdapter(this::handleTvClick)
//        binding.similarTvsList.adapter = similarTvItemsAdapter
//        binding.similarTvsList.isNestedScrollingEnabled = false
//
//        videosController = VideosController {}
//        binding.videosList.adapter = videosController.adapter
//        binding.videosList.isNestedScrollingEnabled = false
    }

//    private fun handleSeasonPickerSelectClick() {
//        val details = tvShowDetailsViewModel.details.value?.data
//        if (details != null) {
//            val seasonNames =
//                details.seasons.mapIndexed { _, season -> season.name } as ArrayList<String>
//
//            val itemPickerFragment: ItemPickerFragment =
//                ItemPickerFragment.newInstance(seasonNames,
//                    tvShowDetailsViewModel.selectedSeasonNameIndexPair.value?.second!!)
//            itemPickerFragment.showsDialog = true
//            itemPickerFragment.show(supportFragmentManager, "pickerDialog")
//            itemPickerFragment.setItemClickListener { newSelectedPosition ->
//                val selectedSeason = details.seasons[newSelectedPosition]
//                tvShowDetailsViewModel.selectedSeasonNameIndexPair.value =
//                    Pair(selectedSeason.name, newSelectedPosition)
//                lifecycleScope.launch {
//                    tvShowDetailsViewModel.fetchSeasonDetails(tvId!!, selectedSeason.seasonNumber)
//                }
//            }
//        }
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun setupViewModel() {
//        tvShowDetailsViewModel.details.observe(this) {
//            val loading = (it!!.isLoading && it.data == null)
//            if (loading) {
//                setLoading(true)
//            } else if (it.data != null) {
//                setLoading(false)
//                updateDetails(it.data)
//
//                // Similar TV Shows
//                similarTvItemsAdapter.submitList(it.data.similar.results)
//                similarTvItemsAdapter.notifyDataSetChanged()
//
//                // Videos
//                checkAndLoadVideo(it.data.videos.results)
//                videosController.setData(it.data.videos.results)
//            }
//        }
//
//        tvShowDetailsViewModel.selectedSeasonNameIndexPair.observe(this) {
//            if (it != null) {
//                binding.selectedSeasonText.text = it.first
//            }
//        }
//
//        tvShowDetailsViewModel.selectedSeasonDetails.observe(this) {
//            if (it.data != null) {
//                episodeItemsAdapter.submitList(it.data.episodes)
//            }
//        }
//    }

    private fun setLoading(flag: Boolean) {
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

//    private fun fetchInitialData() {
//        if (tvId != null) {
//            tvShowDetailsViewModel.fetchTvShowDetails(tvId!!)
//        }
//    }

//    private fun updateDetails(details: TvDetailsResponse) {
//        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
//            .into(binding.thumbnail.backdropImage)
//        views.header.titleText.text = details.name
//        views.header.overviewText.text = details.overview
//        views.header.yearText.text = details.getFirstAirDate()
//        views.header.runtimeText.visibility = View.GONE
//        views.header.ratingText.text = details.voteAverage.toString()
//    }
    
//    private fun checkAndLoadVideo(videos: List<Video>) {
//        val firstVideo =
//            videos.firstOrNull { video -> (video.type == "Trailer") && video.site == "YouTube" }
//        if (firstVideo != null) {
//            if (!bannerVideoLoaded) {
//                binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
//                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
//                        player = youTubePlayer
//                        youTubePlayer.loadVideo(firstVideo.key, 0f)
//                        bannerVideoLoaded = true
//                    }
//                })
//            }
//        } else {
//            binding.thumbnail.playContainer.hide()
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getBinding(): ActivityTvDetailsScreenBinding {
        return ActivityTvDetailsScreenBinding.inflate(layoutInflater)
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
            if (!isVideoRestarted) {
                youTubePlayer.mute()
            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                views.youtubePlayerView.hide()
                views.thumbnail.container.show()
                views.thumbnail.videoLoader.hide()
            }
        }
    }

    private val tabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    views.seasonPicker.show()
                    views.similarTvsList.hide()
                    views.episodesList.show()
                    views.videosList.hide()
                    views.tabContentLoader.hide()
                }
                1 -> {
                    views.seasonPicker.hide()
                    views.episodesList.hide()
                    views.similarTvsList.show()
                    views.videosList.hide()
                }
                2 -> {
                    views.seasonPicker.hide()
                    views.similarTvsList.hide()
                    views.episodesList.hide()
                    views.videosList.show()
                    views.tabContentLoader.hide()
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
}