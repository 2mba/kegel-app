package org.tumba.kegel_app.config

object ConfigConstants {

    const val adsEnabled = "ads_enabled"

    // Interstitial ads config
    const val interstitialAdsEnabled = "interstitial_ads_enabled"
    const val interstitialAdsMinExercisesNumber = "interstitial_ads_min_exercises_number"
    const val interstitialAdsFrequency = "interstitial_ads_frequency"

    // Banner ad
    const val exerciseBannerAdsEnabled = "exercise_banner_ads_enabled"
    const val exerciseBannerAdsMinExercisesNumber = "exercise_banner_ads_min_exercises_number"

    // Free period
    const val defaultFreePeriodDays = "default_free_period_days"
    const val adRewardedFreePeriodDays = "ad_rewarded_free_period_days"

    // Free period suggestion dialog
    const val freePeriodSuggestionEnabled = "free_period_suggestion_dialog_enabled"
    const val freePeriodSuggestionDialogStart = "free_period_suggestion_dialog_start"
    const val freePeriodSuggestionDialogFrequency = "free_period_suggestion_dialog_frequency"
}