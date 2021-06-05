package org.tumba.kegel_app.analytics

import javax.inject.Inject

class AdsTracker @Inject constructor(private val analytics: Analytics) : TrackerScope {

    fun trackInterstitialAdShown() {
        analytics.track("interstitial_ad_shown")
    }

    fun trackInterstitialAdLoaded() {
        analytics.track("interstitial_ad_loaded")
    }

    fun trackInterstitialAdLoadFailed() {
        analytics.track("interstitial_ad_load_failed")
    }

    fun trackExerciseBannerAdShown() {
        analytics.track("interstitial_ad_shown")
    }
}
