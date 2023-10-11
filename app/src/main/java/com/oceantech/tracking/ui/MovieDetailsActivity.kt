package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.tabs.TabLayout
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
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
import javax.inject.Inject

class MovieDetailsActivity : TrackingBaseActivity<ActivityMovieDetailsBinding>(),
    HomeViewModel.Factory {

    var exoPlayer: ExoPlayer? = null
    var isFullScreen = false
    var isLock = false
    private lateinit var bt_fullscreen: ImageView
    private lateinit var bt_lockscreen: ImageView
    var handler: Handler? = null


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
        setUpPlayVideo()


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

    private fun setUpPlayVideo() {
        handler = Handler(Looper.getMainLooper())
        // nút chuyển đổi với biểu tượng thay đổi toàn màn hình hoặc thoát toàn màn hình
        // màn hình có thể xoay dựa trên cảm biến hướng góc của bạn
        bt_fullscreen = findViewById(R.id.bt_fullscreen)
        bt_lockscreen = findViewById(R.id.exo_lock)
        // nút chuyển đổi với biểu tượng thay đổi toàn màn hình hoặc thoát toàn màn hình
        // màn hình có thể xoay dựa trên cảm biến hướng góc của bạn
        bt_fullscreen.setOnClickListener {
            requestedOrientation = if (!isFullScreen) {
                bt_fullscreen.setImageDrawable(
                    ContextCompat
                        .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit)
                )
                views.toolbar.hide()
                views.content.hide()
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                bt_fullscreen.setImageDrawable(
                    ContextCompat
                        .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen)
                )
                views.toolbar.show()
                views.content.show()
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
            //method for toggle will do next
            lockScreen(isLock)
        }

        //ví dụ trình phát có thời lượng tua lại 5 giây hoặc tua đi 5 giây
        //5000 mili giây = 5 giây
        //5000 millisecond = 5 second
        exoPlayer = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        views.player.setPlayer(exoPlayer)
        //screen alway active
        views.player.setKeepScreenOn(true)
        exoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //when data video fetch stream from internet
                if (playbackState == Player.STATE_BUFFERING) {
                    views.progressBar.setVisibility(View.VISIBLE)
                } else if (playbackState == Player.STATE_READY) {
                    //then if streamed is loaded we hide the progress bar
                    views.progressBar.setVisibility(View.GONE)
                }
                if (!exoPlayer!!.playWhenReady) {
                    handler!!.removeCallbacks(updateProgressAction)
                } else {
                    onProgress()
                }
            }
        })
        views.header.playLl.setOnClickListener {
            isFullScreen = !isFullScreen
            views.videoPlayerView.show()
            bt_fullscreen.setImageDrawable(
                ContextCompat
                    .getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit)
            )
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            views.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT); // Đặt giá trị RESIZE_MODE_FIT
            views.thumbnail.container.hide()
            views.youtubePlayerView.hide()
            views.toolbar.hide()
            views.content.hide()

            //pass the video link and play
            val videoUrl = Uri.parse("https://vie2.opstream7.com/20230904/966_6adc7641/index.m3u8")
            val media = MediaItem.fromUri(videoUrl)
            exoPlayer!!.setMediaItem(media)
            exoPlayer!!.prepare()
            exoPlayer!!.play()
        }
    }

    private val updateProgressAction = Runnable { onProgress() }
    private fun onProgress() {
        val player = exoPlayer
        val position = player?.currentPosition ?: 0
        handler!!.removeCallbacks(updateProgressAction)
        val playbackState = player?.playbackState ?: Player.STATE_IDLE
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            var delayMs: Long
            if (player!!.playWhenReady && playbackState == Player.STATE_READY) {
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
//        views.header.playLl.setOnClickListener {
//            playVideo()
//        }

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

    private fun playVideo() {
        if (player != null) {
            player!!.seekTo(0f)
            lifecycleScope.launch {
                delay(500)
                views.youtubePlayerView.show()
                views.thumbnail.container.hide()
            }
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

    override fun onStop() {
        super.onStop()
        exoPlayer!!.stop()
    }


    override fun onPause() {
        super.onPause()
        exoPlayer!!.pause()
    }

}