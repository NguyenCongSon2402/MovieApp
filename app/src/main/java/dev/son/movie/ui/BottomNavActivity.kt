package dev.son.movie.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.viewModel
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityBottomNavBinding
import dev.son.movie.ui.home.HomeFragment
import dev.son.movie.ui.comingsoon.ComingSoonFragment
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.utils.applyExitMaterialTransform
import dev.son.movie.utils.applyMaterialTransform
import dev.son.moviestreamhub.screens.DownloadsFragment
import kotlinx.coroutines.delay
import javax.inject.Inject


@Suppress("DEPRECATION")
class BottomNavActivity : TrackingBaseActivity<ActivityBottomNavBinding>(), HomeViewModel.Factory {


    // Flags to know whether bottom tab fragments are displayed at least once
    private val fragmentFirstDisplay = mutableListOf(false, false, false)

    private val homeFragment = HomeFragment()
    private val comingSoonFragment = ComingSoonFragment()
    private val downloadsFragment = DownloadsFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    private val homeViewModel: HomeViewModel by viewModel()

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        setTheme(R.style.Base_Theme_MovieStreamHub)
        super.onCreate(savedInstanceState)
        getData()
        setupUI()
    }

    private fun getData() {
        homeViewModel.handle(HomeViewAction.getHome)
        homeViewModel.handle(HomeViewAction.getPhimBo)
        homeViewModel.handle(HomeViewAction.getPhimLe)
        homeViewModel.handle(HomeViewAction.getPhimHoatHinh)
        homeViewModel.handle(HomeViewAction.getTvShows)
        homeViewModel.handle(HomeViewAction.getVietSub)
        homeViewModel.handle(HomeViewAction.getThuyetMinh)
        homeViewModel.handle(HomeViewAction.getPhimLongTieng)
        homeViewModel.handle(HomeViewAction.getPhimBoDangChieu)
        homeViewModel.handle(HomeViewAction.getPhimBoDaHoanThanh)
    }

    override fun getBinding(): ActivityBottomNavBinding {
        return ActivityBottomNavBinding.inflate(layoutInflater)
    }

    private fun setupUI() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, downloadsFragment, "downloads").hide(downloadsFragment)
            add(R.id.container, comingSoonFragment, "coming_soon").hide(comingSoonFragment)
            add(R.id.container, homeFragment, "home")
        }.commit()
        views.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }

                R.id.coming_soon -> {
                    if (!fragmentFirstDisplay[1]) {
                        fragmentFirstDisplay[1] = true
                        comingSoonFragment.onFirstDisplay()
                    }
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(comingSoonFragment).commit()
                    activeFragment = comingSoonFragment

                    true
                }

                R.id.downloads -> {
                    if (!fragmentFirstDisplay[2]) {
                        fragmentFirstDisplay[2] = true
                        downloadsFragment.onFirstDisplay()
                    }
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(downloadsFragment).commit()
                    activeFragment = downloadsFragment

                    true
                }

                else -> false
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
//            fragmentFirstDisplay[0] = true
//            fragment.onFirstDisplay()
        }
    }

    fun onFeedFragmentViewCreated() {
        if (!fragmentFirstDisplay[0]) {
            fragmentFirstDisplay[0] = true
            homeFragment.onFirstDisplay()
        }
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }
}