package dev.son.movie.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.viewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityBottomNavBinding
import dev.son.movie.network.models.user.UserId
import dev.son.movie.ui.home.HomeFragment
import dev.son.movie.ui.comingsoon.ComingSoonFragment
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.login.AuthViewState
import dev.son.moviestreamhub.screens.DownloadsFragment
import dev.son.moviestreamhub.screens.MoreFragment
import javax.inject.Inject


@Suppress("DEPRECATION")
class BottomNavActivity : TrackingBaseActivity<ActivityBottomNavBinding>(), HomeViewModel.Factory,
    AuthViewModel.Factory {
    private var token: String? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardAmount: Int? = 0
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    private final var TAG = "BottomNavActivity"
    private val userID: String?
        get() = intent.extras?.getString("userId")


    private lateinit var userId: UserId

    // Flags to know whether bottom tab fragments are displayed at least once
    private val fragmentFirstDisplay = mutableListOf(false, false, false)

    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory

    private val homeFragment = HomeFragment()
    private val comingSoonFragment = ComingSoonFragment()
    private val downloadsFragment = DownloadsFragment()
    private val moreFragment = MoreFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    private val homeViewModel: HomeViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        setTheme(R.style.Base_Theme_MovieStreamHub)
        super.onCreate(savedInstanceState)
        //loadAndShowRewardedInterstitialAd()
        getData()
        setupUI()
    }

    private fun showRewardedInterstitialAd() {

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem ->
                // Handle the reward.
                rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                val updateData = HashMap<String, Any>()
                updateData["coins"] = rewardAmount!! + userId.coins!!
                //loginViewModel.handle(LoginViewAction.upDateUser(userID, updateData))
                Log.d(TAG, "User earned the reward.${rewardAmount}-$rewardType")
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    private fun loadAndShowRewardedInterstitialAd() {
        RewardedAd.load(
            this,
            "ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdsError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdsError)
                    Log.e(TAG, "onAdFailedToLoad: ${loadAdsError.message}")
                    //mRewardedInterstitialAd = null
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.e(TAG, "onLoadAds")
                    //mRewardedInterstitialAd = rewardedInterstitialAd
                    rewardedAd = ad
                    showRewardedInterstitialAd()
                }
            }
        )
    }


    private fun getData() {
        token= userPreferences.token
        homeViewModel.handle(HomeViewAction.getGenre)
        homeViewModel.handle(HomeViewAction.getCountries)
    }

    override fun getBinding(): ActivityBottomNavBinding {
        return ActivityBottomNavBinding.inflate(layoutInflater)
    }

    private fun setupUI() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, downloadsFragment, "downloads").hide(downloadsFragment)
            add(R.id.container, comingSoonFragment, "coming_soon").hide(comingSoonFragment)
            add(R.id.container, moreFragment, "more").hide(moreFragment)
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

                R.id.more -> {
                    if (!fragmentFirstDisplay[2]) {
                        fragmentFirstDisplay[2] = true
                        moreFragment.onFirstDisplay()
                    }
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(moreFragment).commit()
                    activeFragment = moreFragment

                    true
                }

                else -> false
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
            fragmentFirstDisplay[0] = true
            fragment.onFirstDisplay()
        }
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}