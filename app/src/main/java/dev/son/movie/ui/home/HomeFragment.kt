package dev.son.movie.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.netflixclone.constants.CATEGORIES
import com.netflixclone.constants.COUNTRIES
import dev.son.movie.R
import dev.son.movie.adapters.MainEpoxyController
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.databinding.FragmentFeedBinding
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.search.SearchActivity
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import kotlin.math.min


class HomeFragment : TrackingBaseFragment<FragmentFeedBinding>() {

    private lateinit var mainEpoxyController: MainEpoxyController
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var genres: List<Genre>? = null
    private val listData: MutableList<ApiResponse<List<Movie>>?> = MutableList(8) { null }
    private var dataCountries: List<Genre>? = null
    private var dataCategory: List<Genre>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.loader.root.show()
        views.loader.root.startShimmer()
        setupUI()
    }

    override fun onFirstDisplay() {
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(inflater, container, false)
    }

    private fun handleSearchClick() {
        val intent = Intent(requireActivity(), SearchActivity::class.java)
        startActivity(intent)
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


        mainEpoxyController = MainEpoxyController(this::handleMediaClick)
        views.feedItemsList.adapter = mainEpoxyController.adapter
        views.feedItemsList.setItemViewCacheSize(50)

//countries
        views.countriesTv.setOnClickListener {
            dataCountries?.let { it1 ->
                val fragment = ItemPickerFragment.newInstance(it1, COUNTRIES)
                fragment.show(childFragmentManager, "ItemPickerFragmentTag")
            } ?: run {
                Toast.makeText(requireContext(), "Dữ liệu không có sẵn", Toast.LENGTH_SHORT).show()
            }
        }

//categories
        views.categoriesTv.setOnClickListener {
            dataCategory?.let { it1 ->
                val fragment = ItemPickerFragment.newInstance(it1, CATEGORIES)
                fragment.show(childFragmentManager, "ItemPickerFragmentTag1")
            } ?: run {
                Toast.makeText(requireContext(), "Dữ liệu không có sẵn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleMediaClick(items: Movie, posterItems: View) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        if (items.genre.contains("@single")) {
            intent.putExtra("single", true)
        }
        intent.putExtra("movie", items)


        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            posterItems,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }


    private fun calculateAndSetListTopPadding() {
        views.appBarLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val availableHeight: Int = views.appBarLayout.measuredHeight
                if (availableHeight > 0) {
                    views.appBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
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

    override fun invalidate(): Unit = withState(homeViewModel) { it ->
        when (it.genre) {
            is Success -> {
                genres = it.genre.invoke().data
                val (genresWithAt, genresWithoutAt) = genres?.partition { genre ->
                    genre.code?.contains("@") == true
                } ?: Pair(emptyList(), emptyList())

// In danh sách có chứa "@"
                Log.e("Genres with @", genresWithAt.toString())

// In danh sách không chứa "@"
                Log.e("Genres without @", genresWithoutAt.toString())

                dataCategory = genresWithoutAt
                genresWithAt.forEach { genresWithAt ->
                    when (genresWithAt.code) {
                        "@series" -> {
                            homeViewModel.handle(HomeViewAction.getMoviePhimBo(genresWithAt.code))
                        }

                        "@single" -> {
                            homeViewModel.handle(HomeViewAction.getMoviePhimLe(genresWithAt.code))
                        }

                        "@hoathinh" -> {
                            homeViewModel.handle(HomeViewAction.getMoviePhimHoatHinh(genresWithAt.code))
                        }

                        "@tvshows" -> {
                            homeViewModel.handle(HomeViewAction.getMovieTvShow(genresWithAt.code))
                        }

                        "@vietSub" -> {
                            homeViewModel.handle(HomeViewAction.getMovieVietSub(genresWithAt.code))
                        }

                        "@thuyetminh" -> {
                            homeViewModel.handle(HomeViewAction.getMovieThuyetMinh(genresWithAt.code))
                        }

                        "@dangchieu" -> {
                            homeViewModel.handle(HomeViewAction.getMovieDangChieu(genresWithAt.code))
                        }

                        "@hoanthanh" -> {
                            homeViewModel.handle(HomeViewAction.getMovieHoanThanh(genresWithAt.code))
                        }
                    }
                }
                homeViewModel.handleRemoveStateGetGenre()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "genre " +
                            checkStatusApiRes(it.genre),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateGetGenre()
            }

            else -> {}
        }
        when (it.countriesMovies) {
            is Success -> {
                dataCountries = it.countriesMovies.invoke().data
                homeViewModel.handleRemoveStateCountries()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "countriesMovies " +
                            checkStatusApiRes(it.countriesMovies),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateCountries()
            }

            else -> {}
        }
        when (it.moviePhimBo) {
            is Success -> {
                val data = it.moviePhimBo.invoke().apply {
                    this.titlePage = "Phim Bộ"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 0
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBo()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "phimBo " +
                            checkStatusApiRes(it.moviePhimBo),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateGetGenre()
            }

            else -> {}
        }
        when (it.moviePhimLe) {
            is Success -> {
                val data = it.moviePhimLe.invoke().apply {
                    this.titlePage = "Phim Lẻ"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 1
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimle()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "phimLe " +
                            checkStatusApiRes(it.moviePhimLe),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimle()
            }

            else -> {}
        }
        when (it.phimHoatHinh) {
            is Success -> {
                val data = it.phimHoatHinh.invoke().apply {
                    this.titlePage = "Phim Hoạt Hình"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 2
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimHoatHinh()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "phimHoatHinh " +
                            checkStatusApiRes(it.phimHoatHinh),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimHoatHinh()
            }

            else -> {}
        }
        when (it.movieTvShow) {
            is Success -> {
                val data = it.movieTvShow.invoke().apply {
                    this.titlePage = "Tv Show"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 3
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateTvShows()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "tvShow " +
                            checkStatusApiRes(it.movieTvShow),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateTvShows()
            }

            else -> {}
        }
        when (it.movieVietSub) {
            is Success -> {
                val data = it.movieVietSub.invoke().apply {
                    this.titlePage = "VietSub"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 4
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateVietsub()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "VietSub " +
                            checkStatusApiRes(it.movieVietSub),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateTvShows()
            }

            else -> {}
        }
        when (it.movieThuyetMinh) {
            is Success -> {
                val data = it.movieThuyetMinh.invoke().apply {
                    this.titlePage = "Phim Thuyết Minh"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 5
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateThuyetMinh()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "ThuyetMinh " +
                            checkStatusApiRes(it.movieThuyetMinh),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateThuyetMinh()
            }

            else -> {}
        }
        when (it.movieDangChieu) {
            is Success -> {
                val data = it.movieDangChieu.invoke().apply {
                    this.titlePage = "Phim Bộ Đang Chiếu"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 6
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoDangChieu()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "dangChieu " +
                            checkStatusApiRes(it.movieDangChieu),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBoDangChieu()
            }

            else -> {}
        }
        when (it.movieHoanThanh) {
            is Success -> {
                val data = it.movieHoanThanh.invoke().apply {
                    this.titlePage = "Phim Bộ Hoàn Thành"
                }
                if (!data.data.isNullOrEmpty()) {
                    val index = 7
                    if (index < listData.size) {
                        data.let { it1 -> listData[index] = it1 }
                    } else {
                        data.let { it1 -> listData.add(it1) }
                    }
                    mainEpoxyController.setData(listData)
                }

                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            is Fail -> {
                Toast.makeText(
                    activity,
                    "haonThanh " +
                            checkStatusApiRes(it.movieHoanThanh),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            else -> {}
        }
    }
}

