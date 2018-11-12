package com.jishindev.android.xkcd.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.adsnative.ads.ANAdListener
import com.adsnative.ads.ANNativeAd
import com.adsnative.ads.NativeAdUnit

fun Context.getSingleNativeAd(view: View, onAdLoaded: (NativeAdUnit) -> Unit) {
    val nativeAd = ANNativeAd(this, "2Pwo1otj1C5T8y6Uuz9v-xbY1aB09x8rWKvsJ-HI")
    nativeAd.setNativeAdListener(object : ANAdListener {
        override fun onAdFailed(p0: String?) {
            Log.d("AdsUtil", "onAdFailed() called with: p0 = [$p0]")
        }

        override fun onAdClicked(p0: NativeAdUnit?): Boolean {
            Log.d("AdsUtil", "onAdClicked() called with: p0 = [$p0]")
            Toast.makeText(this@getSingleNativeAd, "Ad clicked", Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onAdImpressionRecorded() {
            Log.d("AdsUtil", "onAdImpressionRecorded() called")
        }

        override fun onAdLoaded(nativeAdUnit: NativeAdUnit?) {
            Log.d("AdsUtil", "onAdLoaded() called with: nativeAdUnit = [${nativeAdUnit?.adChoicesClickThroughUrl}]")
            nativeAdUnit ?: return
            nativeAd.attachViewForInteraction(nativeAdUnit, view)
            onAdLoaded(nativeAdUnit)
        }
    })
    nativeAd.loadAd()
}