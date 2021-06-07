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

    fun trackInterstitialAdImpression() {
        analytics.track("interstitial_ad_impression")
    }

    fun trackExerciseBannerAdLoaded() {
        analytics.track("exercise_banner_ad_loaded")
    }

    fun trackExerciseBannerAdOpened() {
        analytics.track("exercise_banner_ad_opened")
    }

    fun trackExerciseBannerAdClicked() {
        analytics.track("exercise_banner_ad_clicked")
    }

    fun trackExerciseBannerAdImpression() {
        analytics.track("exercise_banner_ad_impression")
    }

    fun trackExerciseBannerAdLoadFailed() {
        analytics.track("exercise_banner_ad_load_failed")
    }
}
