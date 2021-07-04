package org.tumba.kegel_app.analytics

import javax.inject.Inject

class HomeScreenTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "HomeScreen")

    fun trackFirstTimeCustomizeClicked() {
        tracker.track(click("home_customize_first_time_exercise"))
    }

    fun trackCustomizeClicked() {
        tracker.track(click("home_customize_exercise"))
    }

    fun trackStartCustomExercise() {
        tracker.track(click("start_custom_exercise"))
    }

    fun trackDefaultFreePeriodExpiredShown() {
        tracker.track("default_free_period_expired_shown")
    }

    fun trackAdRewardFreePeriodExpiredShown() {
        tracker.track("ad_reward_free_period_expired_shown")
    }

    fun trackDefaultFreePeriodExpiredOkClicked() {
        tracker.track(click("default_free_period_expired_yes"))
    }

    fun trackAdRewardFreePeriodExpiredOkClicked() {
        tracker.track(click("ad_rwrd_free_period_expired_yes"))
    }

    fun trackDefaultFreePeriodExpiredCancelled() {
        tracker.track(click("default_free_period_expired_cancel"))
    }

    fun trackAdRewardFreePeriodExpiredCancelled() {
        tracker.track(click("ad_rwrd_free_period_expired_cancel"))
    }

    fun trackNavigateToProUpgradeFromExpiredDialog() {
        tracker.track("navigate_to_pro_upgrade_expired_popup")
    }
}
