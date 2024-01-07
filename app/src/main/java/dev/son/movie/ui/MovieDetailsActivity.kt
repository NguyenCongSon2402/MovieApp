package dev.son.movie.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.material.tabs.TabLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.CommentAdapter
import dev.son.movie.adapters.EpisodeItemsAdapter
import dev.son.movie.adapters.MoviesAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityMovieDetailsBinding
import dev.son.movie.manager.DemoUtil
import dev.son.movie.manager.DownloadTracker
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.postcomment.Comment
import dev.son.movie.network.models.rate.RateRespone
import dev.son.movie.network.models.user.User
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.extractVideoIdFromUrl
import dev.son.movie.utils.getListCodesByCodes
import dev.son.movie.utils.hide
import dev.son.movie.utils.hideKeyboard
import dev.son.movie.utils.isNetworkAvailable
import dev.son.movie.utils.show
import dev.son.movie.utils.showDownloadConfirmationDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("DEPRECATION")
class MovieDetailsActivity : TrackingBaseActivity<ActivityMovieDetailsBinding>(),
    HomeViewModel.Factory {

    var exoPlayer: ExoPlayer? = null
    private var defaultHttpDataSourceFactory: DataSource.Factory? = null
    private var mediaSource: HlsMediaSource? = null
    var isFullScreen = false
    var isLock = false
    private lateinit var bt_fullscreen: ImageView
    private lateinit var bt_lockscreen: ImageView
    private lateinit var progress_bar: ProgressBar
    private lateinit var sec_mid: LinearLayout
    private lateinit var sec_bottom: LinearLayout
    var handler: Handler? = null
    private lateinit var mFullScreenDialog: Dialog

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var episodeItemsAdapter: EpisodeItemsAdapter
    private lateinit var similarMoviesItemsAdapter: MoviesAdapter
    private lateinit var commentAdapter: CommentAdapter
    private var listData: MutableList<Comment> = mutableListOf()
    private var movie: Movie? = null
        get() = intent.getParcelableExtra("movie")
    private val isComingSoon: Boolean
        get() = intent.getBooleanExtra("comingson", false)
    private var favorite: Boolean? = false
    private var checkShow: Boolean = false
    private var currentUser: User = User()
    private var isDownloaded = false
    private var coins: Int = 0

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory


    @Inject
    lateinit var userPreferences: UserPreferences

    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false
    private var media: MediaItem? = null

    private var downloadTracker: DownloadTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        showLoader(true)
        fetchData()
        setUpPlayVideo()
        setupUI()
        resultData()
        updateDetails()
    }

    private fun resultData() {
        homeViewModel.subscribe(this) {
            when (it.getMoviesRecommendation) {
                is Success -> {
                    homeViewModel.handleRemoveStateCategoriesMovies()
                    updateSimilarMovies(it.getMoviesRecommendation.invoke())
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        getString(checkStatusApiRes(it.getMoviesRecommendation)),
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateCategoriesMovies()
                }

                else -> {}
            }
            when (it.movieRateRes) {
                is Success -> {
                    homeViewModel.handleRemoveStateMovieRateRes()
                    if (it.movieRateRes.invoke().data?.id != null)
                        updateMoviesRateRes(it.movieRateRes.invoke())
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.movieRateRes)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateMovieRateRes()
                }

                else -> {}
            }
            when (it.setmovieRate) {
                is Success -> {
                    homeViewModel.handleRemoveStateSetmovieRate()
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.setmovieRate)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateSetmovieRate()
                }

                else -> {}
            }
            when (it.addFavorite) {
                is Success -> {
                    homeViewModel.handleRemoveAddFavorite()
                    if (it.addFavorite.invoke().data.id != null) {
                        favorite = true
                        views.header.imgFavorite.setImageResource(R.drawable.ic_favorite)
                    } else {
                        Toast.makeText(
                            this, "Vui lòng thử lại sau", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.addFavorite)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveAddFavorite()
                }

                else -> {}
            }
            when (it.removeFavorite) {
                is Success -> {
                    homeViewModel.handleRemoveStateRemoveFavorite()
                    favorite = false
                    views.header.imgFavorite.setImageResource(R.drawable.ic_unfavorite)

                    Toast.makeText(
                        this, it.removeFavorite.invoke().message, Toast.LENGTH_SHORT
                    ).show()

                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.removeFavorite)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateRemoveFavorite()
                }

                else -> {}
            }
            when (it.movieById) {
                is Success -> {
                    var movie = it.movieById.invoke().data
                    homeViewModel.handleRemoveGetMovieById()
                    if (movie!!.hasFavorite) {
                        views.header.imgFavorite.setImageResource(R.drawable.ic_favorite)
                        favorite = true
                    }
                    views.header.imgRate.setImageResource(R.drawable.ic_star1)
                    views.header.ratingText.text = movie!!.rating.toString()
                    views.header.textViewCountRates.text =
                        movie!!.numberOfReviews.toString() + " đánh giá"
                    views.header.textViewCategoryMovies.text = movie!!.genre
                    views.header.textViewCastMovies.text = movie!!.actors
                    views.header.textViewDirectorMovies.text = movie!!.director
                    views.header.textViewNationMovies.text = movie!!.country
                    views.header.overviewText.text = movie?.description.toString()
                    views.header.yearText.text = movie?.releaseYear.toString()
                    views.header.runtimeText.text = movie?.duration.toString()
                    showLoader(false)

                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.movieById)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveGetMovieById()
                }

                else -> {}
            }
            when (it.getCommentByMovie) {
                is Success -> {
                    listData.clear()
                    val apiResponseComment = it.getCommentByMovie.invoke()
                    listData = apiResponseComment.data.toMutableList()
                    commentAdapter.setData(listData.asReversed())
                    homeViewModel.handleRemoveStateCommentByMovie()
                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.getCommentByMovie)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateCommentByMovie()
                }

                else -> {}
            }
            when (it.createComment) {
                is Success -> {
                    val comment = it.createComment.invoke().data.apply {
                        this.user = currentUser
                    }
                    listData.add(0, comment)
                    commentAdapter.setData(listData)
                    homeViewModel.handleRemoveStateCreateComment()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        "createComment" + getString(checkStatusApiRes(it.createComment)),
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateCreateComment()
                }

                else -> {}
            }
        }
    }

    private fun fetchData() {
//        val categoryList = getListCodesByCodes(movie?.genre.toString())
//        val radomCategory = categoryList.random()

        homeViewModel.handle(HomeViewAction.getMovieRate(movie?.id.toString()))
        homeViewModel.handle(HomeViewAction.getMoviesRecommendation(movie?.id.toString()))
        homeViewModel.handle(HomeViewAction.movieById(movie?.id.toString()))
        homeViewModel.handle(HomeViewAction.getCommentByMovie(movie?.id.toString()))


//get coins
        lifecycleScope.launch {
            userPreferences.coins.collect {
                coins = it ?: 0
                Log.e("COINS", coins.toString())
            }
        }
        lifecycleScope.launch {
            userPreferences.user.collect {
                currentUser = it ?: User()
                Log.e("USER", currentUser.photoURL.toString())
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun setUpPlayVideo() {
        defaultHttpDataSourceFactory = DemoUtil.getDataSourceFactory(this)
        downloadTracker = DemoUtil.getDownloadTracker(this)
        handler = Handler(Looper.getMainLooper())
        bt_fullscreen = findViewById(R.id.bt_fullscreen)
        bt_lockscreen = findViewById(R.id.exo_lock)
        progress_bar = findViewById(R.id.progress_bar)
        sec_mid = findViewById<LinearLayout>(R.id.sec_controlvid1)
        sec_bottom = findViewById<LinearLayout>(R.id.sec_controlvid2)
        bt_fullscreen.setOnClickListener {
            if (!isFullScreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
            isFullScreen = !isFullScreen
        }
        bt_lockscreen.setOnClickListener {
            if (!isLock) {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_baseline_lock
                    )
                )
            } else {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_outline_lock_open
                    )
                )
            }
            isLock = !isLock
            //method for toggle will do next
            lockScreen(isLock)
        }

        exoPlayer =
            ExoPlayer.Builder(this).setSeekBackIncrementMs(5000).setSeekForwardIncrementMs(5000)
                .build()
        views.player.player = exoPlayer
        //screen alway active
        views.player.keepScreenOn = true
        exoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //when data video fetch stream from internet
                if (playbackState == Player.STATE_BUFFERING) {
                    progress_bar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    //then if streamed is loaded we hide the progress bar
                    progress_bar.visibility = View.GONE
                }
                if (!exoPlayer!!.playWhenReady) {
                    handler!!.removeCallbacks(updateProgressAction)
                } else {
                    onProgress()
                }
            }
        })
        views.header.playLl.setOnClickListener {
            val videoURL = movie?.videoURL?.get(0)
            if (videoURL != null) {
                playMovie(videoURL)
            }

        }
    }

    private fun playMovie(videoURL: String) {
        player?.pause()
        views.youtubePlayerView.release()
        views.videoPlayerView.show()
//                views.toolbar.hide()
        views.player.resizeMode =
            AspectRatioFrameLayout.RESIZE_MODE_FIT // Đặt giá trị RESIZE_MODE_FIT
        views.thumbnail.container.hide()
        views.youtubePlayerView.hide()
//                views.content.hide()
        views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
        //pass the video link and play
        mediaSource =
            defaultHttpDataSourceFactory?.let { it1 ->
                HlsMediaSource.Factory(it1).createMediaSource(
                    MediaItem.fromUri(
                        videoURL
                    )
                )
            }
        if (mediaSource != null) {
            exoPlayer?.setMediaSource(mediaSource!!)
        }
        exoPlayer!!.prepare()
        exoPlayer!!.play()
        lifecycleScope.launch {
            userPreferences.upDateCoins(5, false)
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
//        views.toolbar.hide()
//        views.content.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (views.player.parent as ViewGroup).removeView(views.player)
        mFullScreenDialog.addContentView(
            views.player, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        bt_fullscreen.setImageDrawable(
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit)
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
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen)
        )
    }

    private val updateProgressAction = Runnable { onProgress() }
    private fun onProgress() {
        val player = exoPlayer
        val position = player?.currentPosition ?: 0
        Log.e("currentPosition1", position.toString())
        handler!!.removeCallbacks(updateProgressAction)
        val playbackState = player?.playbackState ?: Player.STATE_IDLE
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            var delayMs: Long
            if (player!!.playWhenReady && playbackState == Player.STATE_READY) {
                delayMs = 1000 - position % 1000
                Log.e("currentPosition2", delayMs.toString())
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
        if (lock) {
            sec_mid.visibility = View.GONE
            sec_bottom.visibility = View.GONE
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

    private fun handleMovieClick(items: Movie, posterItems: View) {

        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movie", items)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            this@MovieDetailsActivity,
            posterItems,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())

    }

    private fun handleEpisodeClick(videoURL: String, position: Int) {

        Toast.makeText(this, " Tập ${position + 1}", Toast.LENGTH_SHORT).show()
        playMovie(videoURL)
        download(videoURL, position)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setupUI() {

        // rating click
        views.header.ratingBar.setOnRatingChangeListener { ratingBar, rating ->
            homeViewModel.handle(
                HomeViewAction.setRateMovie(
                    movie?.id.toString(),
                    rating = rating.toInt()
                )
            )
        }

        // show or hide review movie
        views.header.btnShowMoreReview.setOnClickListener {
            if (checkShow == true) {
                views.header.introduceLl.hide()
                views.header.overviewText.maxLines = 2
                views.header.btnShowMoreReview.text = "xem thêm..."
            } else {
                views.header.introduceLl.show()
                views.header.overviewText.maxLines = Int.MAX_VALUE
                views.header.btnShowMoreReview.text = "ẩn bớt..."
            }
            checkShow = !checkShow
        }

        initFullscreenDialog()
        views.toolbar.setNavigationOnClickListener {
            views.youtubePlayerView.release()
            finishAfterTransition()
        }
        views.header.titleText.text = movie?.title
        views.header.txtStateDow.text = getString(R.string.downloads)
        views.header.imgDown.setImageResource(R.drawable.ic_download)
        Glide.with(views.imgUser).load(currentUser.photoURL).centerCrop()
            .error(getDrawable(R.drawable.ic_person)).into(views.imgUser)
        //views.loader.root.show()
        views.content.hide()
        views.youtubePlayerView.hide()
        views.thumbnail.container.hide()
        views.commentContainer.hide()

        if (isComingSoon) {
            views.header.playLl.hide()
            views.header.comingSoonLl.show()
            views.header.downloadLl.hide()
        }
        views.thumbnail.playContainer.setOnClickListener { replayVideo() }
        views.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        views.tabLayout.addOnTabSelectedListener(tabSelectedListener)
        views.header.overviewText.setOnClickListener {
            views.header.overviewText.maxLines = 10
            views.header.overviewText.isClickable = false
        }


        // movie serie
        if (movie?.genre?.contains("@single") == false) {
            views.header.playListLl.show()
            views.header.titleListPlay.text = "Danh sách phát ${movie?.videoURL?.size} tập"
            episodeItemsAdapter = EpisodeItemsAdapter(this::handleEpisodeClick)
            views.header.rvListPlay.adapter = episodeItemsAdapter
            episodeItemsAdapter.submitList(movie?.videoURL)
            views.header.rvListPlay.isNestedScrollingEnabled = true
        }
        similarMoviesItemsAdapter = MoviesAdapter(this::handleMovieClick)
        views.similarMoviesList.adapter = similarMoviesItemsAdapter
        views.similarMoviesList.isNestedScrollingEnabled = true
        commentAdapter = CommentAdapter()
        views.videosList.adapter = commentAdapter.adapter
        views.videosList.setHasFixedSize(true)
        views.videosList.addItemDecoration(
            DividerItemDecoration(
                baseContext, DividerItemDecoration.VERTICAL
            )
        )
        views.videosList.isNestedScrollingEnabled = true


// favorite click
        views.header.imgFavorite.setOnClickListener {
            favorite = !favorite!!
            val imageResource =
                if (favorite == true) R.drawable.ic_favorite else R.drawable.ic_unfavorite
            views.header.imgFavorite.setImageResource(imageResource)
            if (favorite == true) {
                homeViewModel.handle(HomeViewAction.addFavorite(movie?.id.toString()))
            } else {
                homeViewModel.handle(HomeViewAction.removeFavorite(movie?.id.toString()))
            }
        }

        // add comment
        views.layoutTab.setOnClickListener {
            hideKeyboard()
            views.commentTextInput.clearFocus()
        }

//        Glide.with(views.imgUser).load(user?.photoURL).centerCrop()
//            .into(holder.img_user)
        views.addIcon.setOnClickListener {
            val comment = views.commentTextInput.text.toString()
            if (!comment.isNullOrEmpty() && comment.isNotBlank()) {
                views.commentTextInput.setText("")
                hideKeyboard()
                views.commentTextInput.clearFocus()
                homeViewModel.handle(HomeViewAction.createComment(movie?.id.toString(), comment))
            }
        }
    }

    private fun showLoader(flag: Boolean) {
        if (flag) {
            views.loader.root.startShimmer()
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

    @SuppressLint("SetTextI18n")
    private fun updateDetails() {
        showLoader(false)
        // Basic details
        Glide.with(this).load(movie?.posterHorizontal).transform(CenterCrop())
            .into(views.thumbnail.backdropImage)
        views.header.titleText.text = movie?.title.toString()
        views.header.overviewText.text = movie?.description.toString()
        views.header.yearText.text = movie?.releaseYear.toString()
        views.header.runtimeText.text = movie?.duration.toString()
        views.header.ratingText.text = movie?.country
        // Videos
        movie?.let {
            it.videoURL?.let { videoURLs ->
                if (videoURLs.isNotEmpty()) {
                    checkAndLoadVideo(it)
                    download(videoURLs[0], 0)
                } else {
                    // Xử lý khi danh sách video URL rỗng
                }
            } ?: run {
                // Xử lý khi videoURL là null
            }
        }

    }

    private fun download(videoURL: String?, episode: Int) {
        var displayTitle = movie?.title.toString()
        var setMediaId = movie?.id.toString()
        if (movie?.genre?.contains("@single") == false) {
            displayTitle = "Tập ${episode + 1} " + movie?.title.toString()
            setMediaId = movie?.id.toString() + "_$episode"
        }

        media = MediaItem.Builder().setUri(videoURL).setMediaId(setMediaId)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(movie?.posterHorizontal)
                    .setDisplayTitle(displayTitle)
                    .build()
            ).build()
        if (downloadTracker?.isDownloaded(media!!) == true) {
            views.header.downloadLl.hide()
            views.header.downloadedLl.show()
        } else {
            views.header.downloadLl.show()
            views.header.downloadedLl.hide()
        }

        if (downloadTracker?.downloading(media!!) == true) {
            isDownloaded = true
            views.header.txtStateDow.text = getString(R.string.exo_download_downloading)
            views.header.imgDown.setImageResource(R.drawable.ic_downloading)
        } else {
            isDownloaded = false
            views.header.txtStateDow.text = getString(R.string.downloads)
            views.header.imgDown.setImageResource(R.drawable.ic_download)
        }
        views.header.downloadLl.setOnClickListener {
            if (isDownloaded) {
                showDownloadConfirmationDialog(this) {
                    downloadTracker?.removeDownload(Uri.parse(movie?.videoURL?.get(0) ?: ""))
                    views.header.txtStateDow.text = "DownLoad"
                    views.header.imgDown.setImageResource(R.drawable.ic_download)
                }
            } else {
                views.header.txtStateDow.text = getString(R.string.exo_download_downloading)
                views.header.imgDown.setImageResource(R.drawable.ic_downloading)
                val renderersFactory = DemoUtil.buildRenderersFactory(this)
                media?.let { it1 ->
                    downloadTracker?.toggleDownload(
                        supportFragmentManager,
                        it1,
                        renderersFactory,
                        onDownloadCancel = {
                            views.header.txtStateDow.text =getString(R.string.downloads)
                            views.header.imgDown.setImageResource(R.drawable.ic_download)
                        }
                    )
                }
            }
            isDownloaded = !isDownloaded
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSimilarMovies(listMovieSimilar: ApiResponse<List<Movie>>) {
        // Similar movies
        similarMoviesItemsAdapter.submitList(listMovieSimilar.data)
        similarMoviesItemsAdapter.notifyDataSetChanged()
    }

    private fun updateMoviesRateRes(movieRateRes: ApiResponse<RateRespone>) {
        views.header.ratingBar.rating = movieRateRes.data.rating?.toFloat() ?: 0.0f
        views.header.imgRate.setImageResource(R.drawable.ic_star1)
    }

    private fun checkAndLoadVideo(videos: Movie) {
        val trailerUrl = videos.trailerURL
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
            player!!.unMute()
            lifecycleScope.launch {
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
                    Log.e("HAHAHAHAH", "OK")
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
                views.titleComment.hide()
                views.commentContainer.hide()
            } else {
                views.similarMoviesList.hide()
                views.videosList.show()
                views.titleComment.show()
                views.commentContainer.show()
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