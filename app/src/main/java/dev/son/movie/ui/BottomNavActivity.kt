package dev.son.movie.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityBottomNavBinding
import dev.son.movie.ui.home.HomeFragment
import dev.son.movie.ui.comingsoon.ComingSoonFragment
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.ui.login.LoginViewAction
import dev.son.movie.ui.login.LoginViewModel
import dev.son.movie.ui.login.LoginViewState
import dev.son.moviestreamhub.screens.DownloadsFragment
import dev.son.moviestreamhub.screens.MoreFragment
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("DEPRECATION")
class BottomNavActivity : TrackingBaseActivity<ActivityBottomNavBinding>(), HomeViewModel.Factory,
    LoginViewModel.Factory {

    private var rewardedAd: RewardedAd? = null
    private var rewardAmount: Int ?=0
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    private final var TAG = "BottomNavActivity"
    private val userID: String?
        get() = intent.extras?.getString("userId")

    // Flags to know whether bottom tab fragments are displayed at least once
    private val fragmentFirstDisplay = mutableListOf(false, false, false)

    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory

    private val homeFragment = HomeFragment()
    private val comingSoonFragment = ComingSoonFragment()
    private val downloadsFragment = DownloadsFragment()
    private val moreFragment = MoreFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    private val homeViewModel: HomeViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        setTheme(R.style.Base_Theme_MovieStreamHub)
        super.onCreate(savedInstanceState)
        //ads()
        loadAndShowRewardedInterstitialAd()
        resultData()
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
                updateData["coins"] = rewardAmount!!
                loginViewModel.handle(LoginViewAction.upDateUser(userID, updateData))
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

    private fun resultData() {
        loginViewModel.subscribe(this) {
            when (it.getMyList) {
                is Success -> {
                    lifecycleScope.launch {
                        userPreferences.saveMyList(it.getMyList.invoke())
                    }
                }

                is Fail -> {
                    Toast.makeText(this, "Lỗi trong quá trình lấy dữ liệu", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
            when (it.getFavoriteList) {
                is Success -> {
                    lifecycleScope.launch {
                        userPreferences.saveLikeList(it.getFavoriteList.invoke())
                    }
                }

                is Fail -> {
                    Toast.makeText(this, "Lỗi trong quá trình lấy dữ liệu", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
            when (it.upDateUser) {
                is Success -> {
                    lifecycleScope.launch {
                        userPreferences.upDateCoins(rewardAmount!!,true)
                    }
                }

                is Fail -> {

                }

                else -> {}
            }
        }
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
        homeViewModel.handle(HomeViewAction.getCountries)
        homeViewModel.handle(HomeViewAction.getCategory)
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

    override fun create(initialState: LoginViewState): LoginViewModel {
        return loginViewModelFactory.create(initialState)
    }
}