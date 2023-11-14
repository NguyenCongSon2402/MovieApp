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
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.tabs.TabLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.MoviesAdapter
import dev.son.movie.adapters.VideosController
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityMovieDetailsBinding
import dev.son.movie.manager.DemoUtil
import dev.son.movie.manager.DownloadTracker
import dev.son.movie.manager.TrackSelectionDialog
import dev.son.movie.network.models.Slug.Category
import dev.son.movie.network.models.Slug.Item
import dev.son.movie.network.models.Slug.Slug
import dev.son.movie.network.models.categorymovie.CategoryMovie
import dev.son.movie.network.models.home.Items
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.ui.login.LoginViewAction
import dev.son.movie.ui.login.LoginViewModel
import dev.son.movie.ui.login.LoginViewState
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.extractVideoIdFromUrl
import dev.son.movie.utils.getCurrentFormattedDateTimeWithMilliseconds
import dev.son.movie.utils.getCurrentFormattedTime
import dev.son.movie.utils.hide
import dev.son.movie.utils.hideKeyboard
import dev.son.movie.utils.show
import dev.son.movie.utils.showDownloadConfirmationDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.random.Random


@Suppress("DEPRECATION")
class MovieDetailsActivity : TrackingBaseActivity<ActivityMovieDetailsBinding>(),
    HomeViewModel.Factory, LoginViewModel.Factory {

    var exoPlayer: ExoPlayer? = null
    var isFullScreen = false
    var isLock = false
    private lateinit var bt_fullscreen: ImageView
    private lateinit var bt_lockscreen: ImageView
    private lateinit var progress_bar: ProgressBar
    var handler: Handler? = null
    private lateinit var mFullScreenDialog: Dialog

    private var url: String? = null
    private val homeViewModel: HomeViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var similarMoviesItemsAdapter: MoviesAdapter
    private lateinit var videosController: VideosController
    private var listData: MutableList<UserIdComment> = mutableListOf()

    private val movieSlug: String?
        get() = intent.extras?.getString("name")
    private val movieCategory: String?
        get() = intent.extras?.getString("category")
    private val movieID: String?
        get() = intent.extras?.getString("id")
    private val movieName: String?
        get() = intent.extras?.getString("name1")
    private val uri: String?
        get() = intent.extras?.getString("uri")

    var mtList: Boolean? = false
    var favorite: Boolean? = false
    private var idUser: String? = null
    private var user: UserId = UserId()
    private var userComment: UserIdComment = UserIdComment()
    private val movieId1: MovieId1 = MovieId1()
    private var item: Slug = Slug()
    private var isDownloaded = false

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var loginviewmodelFactory: LoginViewModel.Factory

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
        if (!uri.isNullOrEmpty()) {
            movieSlug?.let { homeViewModel.handle(HomeViewAction.getSlug(name = it)) }
            movieID?.let { LoginViewAction.getComment(it) }?.let { loginViewModel.handle(it) }
        } else {
            movieSlug?.let { homeViewModel.handle(HomeViewAction.getSlug(name = it)) }
            movieCategory?.let { homeViewModel.handle(HomeViewAction.getCategoriesMovies(name = it)) }
            movieID?.let { LoginViewAction.getComment(it) }?.let { loginViewModel.handle(it) }
        }
        setUpPlayVideo()
        setupUI()
        homeViewModel.subscribe(this) {
            when (it.slug) {
                is Success -> {
                    homeViewModel.handleRemoveStateSlug()
                    item = it.slug.invoke()
                    showLoader(false)
                    updateDetails(it.slug.invoke())
                    if (!uri.isNullOrEmpty()) {
                        val randomSlug = item.data?.item?.category?.getOrNull(
                            Random.nextInt(
                                item.data?.item?.category?.size ?: 0
                            )
                        )?.slug ?: "hanh-dong"
                        homeViewModel.handle(HomeViewAction.getCategoriesMovies(name = randomSlug))
                    }

                }

                is Fail -> {
                    Toast.makeText(
                        this, getString(checkStatusApiRes(it.slug)), Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateSlug()
                    showLoader(false)
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

        loginViewModel.subscribe(this) {
            when (it.addTolist) {
                is Success -> {
                    saveMyList(it.addTolist.invoke())
                    loginViewModel.handleRemoveStateAddToList()
                }

                is Fail -> {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                    loginViewModel.handleRemoveStateAddToList()
                }

                else -> {}
            }
            when (it.addToFavorite) {
                is Success -> {
                    saveFavorite(it.addToFavorite.invoke())
                    loginViewModel.handleRemoveStateAddToFavorite()
                }

                is Fail -> {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                    loginViewModel.handleRemoveStateAddToFavorite()
                }

                else -> {}
            }
            when (it.getComments) {
                is Success -> {
                    listData = it.getComments.invoke()
                    videosController.setData(listData)
                    loginViewModel.handleRemoveStateGetComment()
                }

                is Fail -> {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                    loginViewModel.handleRemoveStateGetComment()
                }

                else -> {}
            }
            when (it.addComments) {
                is Success -> {
                    listData.add(0, it.addComments.invoke())
                    videosController.setData(listData)
                    loginViewModel.handleRemoveStateAddComment()
                }

                is Fail -> {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                    loginViewModel.handleRemoveStateAddComment()
                }

                else -> {}
            }
        }
    }

    private fun saveFavorite(id: String) {
        lifecycleScope.launch {
            userPreferences.toggleFavoriteMovie(id)
        }
    }

    private fun saveMyList(id: String) {
        lifecycleScope.launch {
            userPreferences.toggleWatchedMovie(id)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpPlayVideo() {
        downloadTracker = DemoUtil.getDownloadTracker(this)
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
            if (!isLock) {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.ic_baseline_lock
                    )
                )
            } else {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.ic_outline_lock_open
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
            player?.pause()
            views.youtubePlayerView.release()
            isFullScreen = !isFullScreen
            views.videoPlayerView.show()
            views.toolbar.hide()
            openFullscreenDialog()
            views.player.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT // Đặt giá trị RESIZE_MODE_FIT
            views.thumbnail.container.hide()
            views.youtubePlayerView.hide()
            views.content.hide()
            views.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
            //pass the video link and play
            val defaultHttpDataSourceFactory = DemoUtil.getDataSourceFactory(this)
            val mediaSource =
                defaultHttpDataSourceFactory?.let { it1 ->
                    HlsMediaSource.Factory(it1).createMediaSource(
                        MediaItem.fromUri(
                            (uri ?: url).toString()
                        )
                    )
                }
            if (mediaSource != null) {
                exoPlayer?.setMediaSource(mediaSource)
            }
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

    private fun handleMovieClick(items: Items, posterItems: View) {
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
            this, posterItems, "my_shared_element"
        )
        startActivity(intent, options.toBundle())

    }

    private fun setupUI() {
        //get Id user
        lifecycleScope.launch {
            userPreferences.userId.collect {
                if (it != null) {
                    idUser = it.userId.toString()
                    user = it
                }
            }
        }

        initFullscreenDialog()
        views.toolbar.setNavigationOnClickListener {
            views.youtubePlayerView.release()
            finishAfterTransition()
        }
        views.header.titleText.text = movieName
        views.header.txtStateDow.text = "DownLoad"
        views.header.imgDown.setImageResource(R.drawable.ic_download)
        Glide.with(views.imgUser).load(user.avatar).centerCrop()
            .error(getDrawable(R.drawable.ic_person)).into(views.imgUser)
        views.loader.root.show()
        views.content.hide()
        views.youtubePlayerView.hide()
        views.thumbnail.container.hide()
        views.commentContainer.hide()
        views.thumbnail.playContainer.setOnClickListener { replayVideo() }
        views.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        views.tabLayout.addOnTabSelectedListener(tabSelectedListener)
        views.header.overviewText.setOnClickListener {
            views.header.overviewText.maxLines = 10
            views.header.overviewText.isClickable = false
        }
        similarMoviesItemsAdapter = MoviesAdapter(this::handleMovieClick)
        views.similarMoviesList.adapter = similarMoviesItemsAdapter
        views.similarMoviesList.isNestedScrollingEnabled = true
        videosController = VideosController()
        views.videosList.adapter = videosController.adapter
        views.videosList.setHasFixedSize(true)
        views.videosList.addItemDecoration(
            DividerItemDecoration(
                baseContext, DividerItemDecoration.VERTICAL
            )
        )
        views.videosList.isNestedScrollingEnabled = true

        // add favorite
        lifecycleScope.launch {
            val movieExists = userPreferences.checkFavoriteMovie(movieID.toString()).first()
            if (movieExists) {
                // Toast.makeText(this@MovieDetailsActivity, "True", Toast.LENGTH_SHORT).show()
                views.header.imgFavorite.setImageResource(R.drawable.ic_favorite)
                favorite = !favorite!!
            } else {
                //Toast.makeText(this@MovieDetailsActivity, "False", Toast.LENGTH_SHORT).show()
                views.header.imgFavorite.setImageResource(R.drawable.ic_unfavorite)
            }
        }

        // add to list
        lifecycleScope.launch {
            val movieExists = userPreferences.checkWatchedMovie(movieID.toString()).first()
            if (movieExists) {
                // Toast.makeText(this@MovieDetailsActivity, "True", Toast.LENGTH_SHORT).show()
                views.header.igmAdd.setImageResource(R.drawable.ic_check)
                mtList = !mtList!!
            } else {
                //Toast.makeText(this@MovieDetailsActivity, "False", Toast.LENGTH_SHORT).show()
                views.header.igmAdd.setImageResource(R.drawable.ic_add)
            }
        }

        // add
        views.header.igmAdd.setOnClickListener {
            mtList = !mtList!!
            val imageResource = if (mtList == true) R.drawable.ic_check else R.drawable.ic_add
            views.header.igmAdd.setImageResource(imageResource)
            movieId1.apply {
                this.movieId1 = movieID
                this.slug = movieSlug
                this.category = ArrayList<Category>().apply {
                    item.data?.item?.category?.get(0)?.let { add(it) }
                }
                this.type = item.data?.item?.type
                this.thumbUrl = item.data?.item?.thumbUrl

            }

            if (!idUser.isNullOrEmpty()) {
                loginViewModel.handle(LoginViewAction.addToList(movieId1, idUser!!))
            }
        }
        views.header.imgFavorite.setOnClickListener {
            favorite = !favorite!!
            val imageResource =
                if (favorite == true) R.drawable.ic_favorite else R.drawable.ic_unfavorite
            views.header.imgFavorite.setImageResource(imageResource)
            movieId1.apply {
                this.movieId1 = movieID
                this.slug = movieSlug
                this.category = ArrayList<Category>().apply {
                    item.data?.item?.category?.get(0)?.let { add(it) }
                }
                this.type = item.data?.item?.type
                this.thumbUrl = item.data?.item?.thumbUrl

            }

            if (!idUser.isNullOrEmpty()) {
                loginViewModel.handle(LoginViewAction.addToFavorite(movieId1, idUser!!))
            }
        }

        // add comment
        views.layoutTab.setOnClickListener {
            hideKeyboard()
            views.commentTextInput.clearFocus()
        }
        views.addIcon.setOnClickListener {
            val comment = views.commentTextInput.text.toString()
            if (!comment.isNullOrEmpty() && comment.isNotBlank()) {
                userComment.apply {
                    this.userId1 = user.userId
                    this.name = user.name
                    this.timestamp = getCurrentFormattedTime()
                    this.text = comment
                    this.commentId = getCurrentFormattedDateTimeWithMilliseconds()
                    this.avatar = user.avatar
                }
                views.commentTextInput.setText("")
                hideKeyboard()
                views.commentTextInput.clearFocus()
                loginViewModel.handle(LoginViewAction.addComment(movieID.toString(), userComment))
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
    private fun updateDetails(data: Slug) {
        url = data.data?.item?.episodes?.get(0)?.serverData?.get(0)?.linkM3u8.toString()
        media = MediaItem.Builder().setUri(url).setMediaId(movieSlug.toString())
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(data.data?.item?.thumbUrl)
                    .setDisplayTitle(data?.data?.item?.name)
                    .build()
            ).build()
        // Basic details
        Glide.with(this).load(data.data?.seoOnPage?.seoSchema?.image).transform(CenterCrop())
            .into(views.thumbnail.backdropImage)
        views.header.titleText.text = data.data?.item?.name
        views.header.overviewText.text = data.data?.item?.content
        views.header.yearText.text = data.data?.item?.year.toString()
        views.header.runtimeText.text = data.data?.item?.time
        views.header.ratingText.text = data.data?.item?.lang
        // Videos
        checkAndLoadVideo(data.data?.item!!)
        //videosController.setData(details.videos.results)
        if (downloadTracker?.isDownloaded(media!!) == true) {
            views.header.downloadLl.hide()
            views.header.downloadedLl.show()
        }

        if (downloadTracker?.downloading(media!!) == true) {
            isDownloaded = true
            views.header.txtStateDow.text = "Downloading"
            views.header.imgDown.setImageResource(R.drawable.ic_downloading)
        }
        views.header.downloadLl.setOnClickListener {
            if (isDownloaded) {
                showDownloadConfirmationDialog(this) {
                    downloadTracker?.removeDownload(Uri.parse(url))
                    views.header.txtStateDow.text = "DownLoad"
                    views.header.imgDown.setImageResource(R.drawable.ic_download)
                }
            } else {
                views.header.txtStateDow.text = "Downloading"
                views.header.imgDown.setImageResource(R.drawable.ic_downloading)
                val renderersFactory = DemoUtil.buildRenderersFactory(this)
                media?.let { it1 ->
                    downloadTracker?.toggleDownload(
                        supportFragmentManager,
                        it1,
                        renderersFactory,
                        onDownloadCancel = {
                            views.header.txtStateDow.text = "DownLoad"
                            views.header.imgDown.setImageResource(R.drawable.ic_download)
                        }
                    )
                }
            }

            isDownloaded = !isDownloaded
        }

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

    override fun create(initialState: LoginViewState): LoginViewModel {
        return loginviewmodelFactory.create(initialState)
    }

}