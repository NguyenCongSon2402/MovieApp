package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.adapters.EpisodeItemsAdapter
import com.oceantech.tracking.adapters.MoviesAdapter

import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.models.Slug.Item
import com.oceantech.tracking.data.models.Slug.ServerData
import com.oceantech.tracking.data.models.Slug.Slug
import com.oceantech.tracking.data.models.categorymovie.CategoryMovie
import com.oceantech.tracking.data.models.home.Items
import com.oceantech.tracking.databinding.ActivityTvDetailsScreenBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.MediaDetailsBottomSheet
import com.oceantech.tracking.utils.applyMaterialTransform
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
import javax.inject.Inject

@Suppress("DEPRECATION")
class TvDetailsActivity : TrackingBaseActivity<ActivityTvDetailsScreenBinding>(),
    HomeViewModel.Factory {
    private lateinit var episodeItemsAdapter: EpisodeItemsAdapter
    private lateinit var similarMoviesItemsAdapter: MoviesAdapter

    //    private lateinit var videosController: VideosController
    private lateinit var mFullScreenDialog: Dialog

    //video
    var exoPlayer: ExoPlayer? = null
    var isFullScreen = false
    var isLock = false
    private lateinit var bt_fullscreen: ImageView
    private lateinit var bt_lockscreen: ImageView
    private lateinit var progress_bar: ProgressBar
    var handler: Handler? = null
    private var url = ""


    private val homeViewModel: HomeViewModel by viewModel()

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val movieSlug: String?
        get() = intent.extras?.getString("name")
    private val movieCategory: String?
        get() = intent.extras?.getString("category")
    private val thumbUrl: String?
        get() = intent.extras?.getString("thumbUrl")
    private val movieId: String?
        get() = intent.extras?.getString("id")


    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        movieSlug?.let { homeViewModel.handle(HomeViewAction.getSlug(name = it)) }
        movieCategory?.let { homeViewModel.handle(HomeViewAction.getCategoriesMovies(name = it)) }
        setupUI()
        homeViewModel.subscribe(this) {
            when (it.slug) {
                is Success -> {
                    homeViewModel.handleRemoveStateSlug()
                    setLoading(false)
                    updateDetails(it.slug.invoke())
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.slug)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateSlug()
                    setLoading(true)
                }

                else -> {}
            }
            when (it.categoriesMovies) {
                is Success -> {
                    homeViewModel.handleRemoveStateCategoriesMovies()
                    updateSimilarMovies(it.categoriesMovies.invoke())
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.categoriesMovies)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateCategoriesMovies()
                }

                else -> {}
            }
        }
        setUpPlayVideo()
    }

    private fun setUpPlayVideo() {
        handler = Handler(Looper.getMainLooper())
        bt_fullscreen = findViewById(R.id.bt_fullscreen)
        bt_lockscreen = findViewById(R.id.exo_lock)
        progress_bar = findViewById(R.id.progress_bar)
        bt_fullscreen.setOnClickListener {
            if (!isFullScreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
            isFullScreen = !isFullScreen
        }
        bt_lockscreen.setOnClickListener {
            //change icon base on toggle lock screen or unlock screen
            if (!isLock) {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock
                    )
                )
            } else {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_outline_lock_open
                    )
                )
            }
            isLock = !isLock
            lockScreen(isLock)
        }

        exoPlayer = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        views.player.setPlayer(exoPlayer)
        //screen alway active
        views.player.setKeepScreenOn(true)
        exoPlayer!!.addListener(object : Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //when data video fetch stream from internet
                if (playbackState == STATE_BUFFERING) {
                    progress_bar.setVisibility(View.VISIBLE)
                } else if (playbackState == STATE_READY) {
                    //then if streamed is loaded we hide the progress bar
                    progress_bar.setVisibility(View.GONE)
                }
                if (!exoPlayer!!.playWhenReady) {
                    handler!!.removeCallbacks(updateProgressAction)
                } else {
                    onProgress()
                }
            }
        })
        views.header.playLl.setOnClickListener {
            player?.pause()
            views.youtubePlayerView.release()
            isFullScreen = !isFullScreen
            views.videoPlayerView.show()
            openFullscreenDialog()
            views.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT); // Đặt giá trị RESIZE_MODE_FIT
            views.thumbnail.container.hide()
            views.youtubePlayerView.hide()
            views.content.hide()
            views.toolbar.hide()
            views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
            //pass the video link and play
            val videoUrl = Uri.parse(url)
            val media = MediaItem.fromUri(videoUrl)
            exoPlayer!!.setMediaItem(media)
            exoPlayer!!.prepare()
            exoPlayer!!.play()
        }
    }

    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (isFullScreen) {
                        closeFullscreenDialog()
                        isFullScreen = !isFullScreen
                    } else {
                        finishAfterTransition() // Chỉ gọi super.onBackPressed() khi không trong chế độ toàn màn hình.
                    }
                }
            }
    }

    private fun openFullscreenDialog() {
        views.toolbar.hide()
        views.content.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (views.player.parent as ViewGroup).removeView(views.player)
        mFullScreenDialog.addContentView(
            views.player,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        bt_fullscreen.setImageDrawable(
            ContextCompat
                .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit)
        )
        mFullScreenDialog.show()
    }

    private fun closeFullscreenDialog() {
        views.toolbar.show()
        views.content.show()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        (views.player.parent as ViewGroup).removeView(views.player)
        (views.videoPlayerView).addView(views.player)
        mFullScreenDialog.dismiss()
        bt_fullscreen.setImageDrawable(
            ContextCompat
                .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen)
        )
    }

    private val updateProgressAction = Runnable { onProgress() }
    private fun onProgress() {
        val player = exoPlayer
        val position = player?.currentPosition ?: 0
        handler!!.removeCallbacks(updateProgressAction)
        val playbackState = player?.playbackState ?: STATE_IDLE
        if (playbackState != STATE_IDLE && playbackState != STATE_ENDED) {
            var delayMs: Long
            if (player!!.playWhenReady && playbackState == STATE_READY) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }
            handler!!.postDelayed(updateProgressAction, delayMs)
        }
    }

    private fun lockScreen(lock: Boolean) {
//just hide the control for lock screen and vise versa

        //just hide the control for lock screen and vise versa
        val sec_mid = findViewById<LinearLayout>(R.id.sec_controlvid1)
        val sec_bottom = findViewById<LinearLayout>(R.id.sec_controlvid2)
        if (lock) {
            sec_mid.visibility = View.INVISIBLE
            sec_bottom.visibility = View.INVISIBLE
        } else {
            sec_mid.visibility = View.VISIBLE
            sec_bottom.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer!!.release()
        views.youtubePlayerView.release()
        views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
        views.menusTabLayout.removeOnTabSelectedListener(tabSelectedListener)
    }

    private fun handleTvClick(items: Items, posterItems: View) {
        val categoryList = items.category
        val shuffledIndices = categoryList.indices.shuffled()
        val randomIndex = shuffledIndices.first()
        val randomCategory = categoryList[randomIndex]
        val randomSlug = randomCategory.slug


        val intent: Intent
        if (items.type == "single") {
            intent = Intent(this, MovieDetailsActivity::class.java)
        } else {
            intent = Intent(this, TvDetailsActivity::class.java)
            intent.putExtra("thumbUrl", items.thumbUrl)
        }

        intent.putExtra("name", items.slug)
        intent.putExtra("category", randomSlug)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            posterItems,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    private fun handleEpisodeClick(Episode: ServerData) {
        player?.pause()
        player?.removeListener(youTubePlayerListener)
        isFullScreen = !isFullScreen
        views.videoPlayerView.show()
        bt_fullscreen.setImageDrawable(
            ContextCompat
                .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit)
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        views.player.resizeMode =
            AspectRatioFrameLayout.RESIZE_MODE_FIT; // Đặt giá trị RESIZE_MODE_FIT
        views.thumbnail.container.hide()
        views.youtubePlayerView.hide()
        views.content.hide()
        views.toolbar.hide()
        views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
        //pass the video link and play
        val videoUrl = Uri.parse(Episode.linkM3u8)
        val media = MediaItem.fromUri(videoUrl)
        exoPlayer!!.setMediaItem(media)
        exoPlayer!!.prepare()
        exoPlayer!!.play()
    }

    private fun setupUI() {
        initFullscreenDialog()
        views.toolbar.setNavigationOnClickListener {
            views.youtubePlayerView.release()
            finishAfterTransition()
        }
        views.loader.root.show()
        views.loader.root.startShimmer()
        views.content.hide()
        views.youtubePlayerView.hide()
        views.thumbnail.container.hide()
        views.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        views.header.overviewText.setOnClickListener {
            views.header.overviewText.maxLines = 10
            views.header.overviewText.isClickable = false
        }
        views.menusTabLayout.addOnTabSelectedListener(tabSelectedListener)

//        views.seasonPicker.setOnClickListener { handleSeasonPickerSelectClick() }

        episodeItemsAdapter = EpisodeItemsAdapter(this::handleEpisodeClick, thumbUrl!!)
        views.episodesList.adapter = episodeItemsAdapter
        views.episodesList.isNestedScrollingEnabled = false

        similarMoviesItemsAdapter = MoviesAdapter(this::handleTvClick)
        views.similarTvsList.adapter = similarMoviesItemsAdapter
        views.similarTvsList.isNestedScrollingEnabled = false
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
    private fun setLoading(flag: Boolean) {
        if (flag) {
            views.loader.root.show()
            views.content.hide()
            views.youtubePlayerView.hide()
            views.thumbnail.container.hide()
        } else {
            views.loader.root.stopShimmer()
            views.loader.root.hide()
            views.content.show()
            views.thumbnail.container.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDetails(data: Slug) {
        url = data.data?.item?.episodes?.get(0)?.serverData?.get(0)?.linkM3u8.toString()
        Glide.with(this).load(data.data?.seoOnPage?.seoSchema?.image).transform(CenterCrop())
            .into(views.thumbnail.backdropImage)
        views.header.titleText.text = data.data?.item?.name
        views.header.overviewText.text = data.data?.item?.content
        views.header.yearText.text = data.data?.item?.year.toString()
        views.header.runtimeText.text = data.data?.item?.time
        views.header.ratingText.text = data.data?.item?.lang

        // Videos
        checkAndLoadVideo(data.data?.item!!)

        episodeItemsAdapter.submitList(data.data?.item?.episodes?.get(0)?.serverData)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSimilarMovies(categoryMovie: CategoryMovie) {
        // Similar movies
        similarMoviesItemsAdapter.submitList(categoryMovie.data?.items)
        similarMoviesItemsAdapter.notifyDataSetChanged()
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
                    delay(50)
                    views.thumbnail.container.hide()
                    views.thumbnail.videoLoader.hide()
                    views.youtubePlayerView.show()
                    delay(1000)
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

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.stop()
        views.youtubePlayerView.release()
    }


    override fun onPause() {
        super.onPause()
        exoPlayer!!.pause()
        views.youtubePlayerView.release()
    }
}