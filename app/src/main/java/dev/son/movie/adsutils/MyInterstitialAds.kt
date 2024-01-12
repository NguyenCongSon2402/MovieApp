package dev.son.movie.adsutils

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MyInterstitialAds(private val activity: Activity) {

    private var interstitialAds : InterstitialAd? = null


    fun loadInterstitialAds(adUnitId: Int, callback: (Boolean) -> Unit) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            activity.getString(adUnitId),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(mInterstitialAds: InterstitialAd) {
                    interstitialAds = mInterstitialAds
                    callback(true) // Gọi callback với tham số true khi quảng cáo được tải thành công
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAds = null
                    callback(false) // Gọi callback với tham số false khi quảng cáo không tải thành công
                }
            }
        )
    }

    fun showInterstitialAds(afterSomeCode : () -> Unit){
        if (interstitialAds != null){
            interstitialAds!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAds = null
                        //Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show()
                        afterSomeCode()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        interstitialAds = null
                        //Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show()
                        afterSomeCode()
                    }

                }

            interstitialAds!!.show(activity)
        }else{
            afterSomeCode()
        }
    }

}