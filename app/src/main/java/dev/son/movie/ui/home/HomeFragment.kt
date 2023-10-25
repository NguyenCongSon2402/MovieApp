package dev.son.movie.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import dev.son.movie.network.models.home.Data
import dev.son.movie.network.models.home.Items
import dev.son.movie.databinding.FragmentFeedBinding
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.search.SearchActivity
import dev.son.movie.ui.TvDetailsActivity
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import timber.log.Timber
import kotlin.math.min


class HomeFragment : TrackingBaseFragment<FragmentFeedBinding>() {

    private lateinit var mainEpoxyController: MainEpoxyController
    private val homeViewModel: HomeViewModel by activityViewModel()

    private val listData: MutableList<Data?> = MutableList(10) { null }
    private var dataCountries: dev.son.movie.network.models.countries.Data? = null
    private var dataCategory: dev.son.movie.network.models.countries.Data? = null

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


        views.countriesTv.setOnClickListener {
            dataCountries?.let { it1 ->
                val fragment = ItemPickerFragment.newInstance(it1, COUNTRIES)
                fragment.show(childFragmentManager, "ItemPickerFragmentTag")
            } ?: run {
                Toast.makeText(requireContext(), "Dữ liệu không có sẵn", Toast.LENGTH_SHORT).show()
            }
        }


        views.categoriesTv.setOnClickListener {
            dataCategory?.let { it1 ->
                val fragment = ItemPickerFragment.newInstance(it1, CATEGORIES)
                fragment.show(childFragmentManager, "ItemPickerFragmentTag1")
            } ?: run {
                Toast.makeText(requireContext(), "Dữ liệu không có sẵn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleMediaClick(items: Items, posterItems: View) {
        val categoryList = items.category
        val shuffledIndices = categoryList.indices.shuffled()
        val randomIndex = shuffledIndices.first()
        val randomCategory = categoryList[randomIndex]
        val randomSlug = randomCategory.slug
        val intent: Intent
        if (items.type == "single") {
            intent = Intent(activity, MovieDetailsActivity::class.java)
        } else {
            intent = Intent(activity, TvDetailsActivity::class.java)
            intent.putExtra("thumbUrl", items.thumbUrl)
        }
        intent.putExtra("name", items.slug)
        intent.putExtra("category", randomSlug)


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

    override fun invalidate(): Unit = withState(homeViewModel) {
        when (it.homes) {
            is Success -> {
                Timber.e("homes")
                val data = it.homes.invoke().data.apply {
                    this?.titlePage = "Phim Mới"
                }
                //Log.e("TAG0homes", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 0
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA0", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateHome()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.homes)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateHome()
            }

            else -> {}
        }
        when (it.phimBo) {
            is Success -> {
                Timber.e("phimBo")
                val data = it.phimBo.invoke().data.apply {
                    this?.titlePage = "Phim Bộ"
                }
                //Log.e("TAG1phimBo", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 1
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBo()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.phimBo)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBo()
            }

            else -> {}
        }
        when (it.phimLe) {
            is Success -> {
                Timber.e("phimLe")
                val data = it.phimLe.invoke().data.apply {
                    this?.titlePage = "Phim Lẻ"
                }
                //Log.e("TAG2phimLe", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 2
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimle()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.phimLe)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimle()
            }

            else -> {}
        }
        when (it.phimHoatHinh) {
            is Success -> {
                Timber.e("phimHoatHinh")
                val data = it.phimHoatHinh.invoke().data.apply {
                    this?.titlePage = "Phim Hoạt hình"
                }
                //Log.e("TAG3phimHoatHinh", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 3
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimHoatHinh()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.phimHoatHinh)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimHoatHinh()
            }

            else -> {}
        }
        when (it.tvShows) {
            is Success -> {
                Timber.e("tvSHow")
                val data = it.tvShows.invoke().data.apply {
                    this?.titlePage = "TV Shows"
                }
                //Log.e("TAG4tvSHow", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 4
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateTvShows()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.tvShows)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateTvShows()
            }

            else -> {}
        }
        when (it.vietsub) {
            is Success -> {
                Timber.e("vietsub")
                val data = it.vietsub.invoke().data.apply {
                    this?.titlePage = "VietSub"
                }
                //Log.e("TAG5vietsub", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 5
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateVietsub()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.vietsub)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateVietsub()
            }

            else -> {}
        }
        when (it.thuyetMinh) {
            is Success -> {
                Timber.e("thuyetminh")
                val data = it.thuyetMinh.invoke().data.apply {
                    this?.titlePage = "Phim Thuyết Minh"
                }
                //Log.e("TAG6thuyetminh", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 6
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateThuyetMinh()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.thuyetMinh)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateThuyetMinh()
            }

            else -> {}
        }
        when (it.longTieng) {
            is Success -> {
                Timber.e("longtieng")
                val data = it.longTieng.invoke().data.apply {
                    this?.titlePage = "Phim Lồng Tiếng"
                }
                //Log.e("TAG7longtieng", "Size${listData.size} data ${data?.titlePage.toString()}")
                val index = 7
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateLongTieng()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.longTieng)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateLongTieng()
            }

            else -> {}
        }
        when (it.phimBoDangChieu) {
            is Success -> {
                Timber.e("phimdangChieu")
                val data = it.phimBoDangChieu.invoke().data.apply {
                    this?.titlePage = "Phim Đang Chiếu"
                }
//                Log.e(
//                    "TAG8phimdangChieu",
//                    "Size${listData.size} data ${data?.titlePage.toString()}"
//                )
                val index = 8
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoDangChieu()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.phimBoDangChieu)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBoDangChieu()
            }

            else -> {}
        }
        when (it.phimBoHoanThanh) {
            is Success -> {
                Timber.e("phimDaHoanThanh")
                val data = it.phimBoHoanThanh.invoke().data.apply {
                    this?.titlePage = "Phim Bộ Đã Hoàn Thành"
                }
//                Log.e(
//                    "TAG9phimDaHoanThanh",
//                    "Size${listData.size} data ${data?.titlePage.toString()}"
//                )
                val index = 9
                if (index < listData.size) {
                    data?.let { it1 -> listData[index] = it1 }
                } else {
                    // Thêm vào cuối danh sách
                    data?.let { it1 -> listData.add(it1) }
                }
//                listData.forEachIndexed { index, data ->
//                    Log.e("DATA", "Index: $index, Title: ${data?.titlePage}")
//                }

                mainEpoxyController.setData(listData)
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.phimBoHoanThanh)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            else -> {}
        }
        when (it.countries) {
            is Success -> {
                Timber.e("countries")
                dataCountries = it.countries.invoke().data
                homeViewModel.handleRemoveStateCountries()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.countries)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateCountries()
            }

            else -> {}
        }
        when (it.category) {
            is Success -> {
                Timber.e("category")
                dataCategory = it.category.invoke().data
                homeViewModel.handleRemoveStateCategories()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.category)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateCategories()
            }

            else -> {}
        }
    }
}

