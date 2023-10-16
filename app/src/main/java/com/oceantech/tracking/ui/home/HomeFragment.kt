package com.oceantech.tracking.ui.home

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
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.adapters.MainEpoxyController
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.models.home.Data
import com.oceantech.tracking.data.models.home.Items
import com.oceantech.tracking.databinding.FragmentFeedBinding
import com.oceantech.tracking.ui.MovieDetailsActivity
import com.oceantech.tracking.ui.TvDetailsActivity
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.hide
import com.oceantech.tracking.utils.show
import timber.log.Timber
import kotlin.math.min


class HomeFragment : TrackingBaseFragment<FragmentFeedBinding>() {

    private lateinit var mainEpoxyController: MainEpoxyController
    private val homeViewModel: HomeViewModel by activityViewModel()

    private val listData: MutableList<Data> = mutableListOf()

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
//        val intent = Intent(requireActivity(), SearchActivity::class.java)
//        startActivity(intent)
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

        views.tvShowsTv.setOnClickListener {
//            val intent = Intent(requireActivity(), PopularTvActivity::class.java)
//            startActivity(intent)
        }

        views.moviesTv.setOnClickListener {
//            val intent = Intent(requireActivity(), PopularMoviesActivity::class.java)
//            startActivity(intent)
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
        //intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            posterItems,
            items.slug
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

    override fun invalidate(): Unit = withState(homeViewModel) {
        when (it.homes) {
            is Success -> {
                Log.e("TAG", "homes")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                val data = it.homes.invoke().data.apply {
                    this?.titlePage = "Phim Mới"
                }
                data?.let { it1 -> listData.add(it1) }
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateHome()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.homes)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStateHome()
            }

            else -> {}
        }
        when (it.phimBo) {
            is Success -> {
                Log.e("TAG", "phimBo")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.phimBo.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBo()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.phimBo)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBo()
            }

            else -> {}
        }
        when (it.phimLe) {
            is Success -> {
                Log.e("TAG", "phimLe")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.phimLe.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimle()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.phimLe)), Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimle()
            }

            else -> {}
        }
        when (it.phimHoatHinh) {
            is Success -> {
                Log.e("TAG", "phimHoatHinh")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.phimHoatHinh.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimHoatHinh()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "tvSHow")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.tvShows.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateTvShows()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "vietsub")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.vietsub.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateVietsub()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "thuyetminh")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.thuyetMinh.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateThuyetMinh()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "longtieng")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.longTieng.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStateLongTieng()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "phimdangChieu")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.phimBoDangChieu.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoDangChieu()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
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
                Log.e("TAG", "phimDaHoanThanh")
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                listData.add(it.phimBoHoanThanh.invoke().data!!)
                mainEpoxyController.categories = listData
                views.loader.root.stopShimmer()
                views.loader.root.hide()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            is Fail -> {
                Timber.e("HomeFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.phimBoHoanThanh)),
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.handleRemoveStatePhimBoHoanThanh()
            }

            else -> {}
        }
    }
}

