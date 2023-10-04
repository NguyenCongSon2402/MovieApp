package com.oceantech.tracking.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivityBottomNavBinding
import com.oceantech.tracking.ui.home.FeedFragment
import com.oceantech.tracking.ui.comingsoon.ComingSoonFragment
import dev.son.moviestreamhub.screens.DownloadsFragment


@Suppress("DEPRECATION")
class BottomNavActivity : TrackingBaseActivity<ActivityBottomNavBinding>() {


    // Flags to know whether bottom tab fragments are displayed at least once
    private val fragmentFirstDisplay = mutableListOf(false, false, false)

    private val feedFragment = FeedFragment()
    private val comingSoonFragment = ComingSoonFragment()
    private val downloadsFragment = DownloadsFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = feedFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        setTheme(R.style.Base_Theme_MovieStreamHub)
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun getBinding(): ActivityBottomNavBinding {
        return ActivityBottomNavBinding.inflate(layoutInflater)
    }

    private fun setupUI() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, downloadsFragment, "downloads").hide(downloadsFragment)
            add(R.id.container, comingSoonFragment, "coming_soon").hide(comingSoonFragment)
            add(R.id.container, feedFragment, "home")
        }.commit()

        views.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(feedFragment).commit()
                    activeFragment = feedFragment
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
        if (fragment is FeedFragment) {
//            fragmentFirstDisplay[0] = true
//            fragment.onFirstDisplay()
        }
    }

    fun onFeedFragmentViewCreated() {
        if (!fragmentFirstDisplay[0]) {
            fragmentFirstDisplay[0] = true
            feedFragment.onFirstDisplay()
        }
    }
}