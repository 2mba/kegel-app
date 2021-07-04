package org.tumba.kegel_app.analytics

import javax.inject.Inject

class ProUpgradeTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "ProUpgrade")

    fun trackClose() {
        tracker.track(click("close_pro_upgrade_screen"))
    }

    fun trackClickBuy() {
        tracker.track(click("get_pro_upgrade"))
    }

    fun trackClickRestorePurchase() {
        tracker.track(click("restore_purchase"))
    }

    fun trackLoadRestorePurchaseSuccessful() {
        tracker.track("restore_purchase_successful")
    }

    fun trackRestorePurchaseFailed() {
        tracker.track("restore_purchase_failed")
    }

    fun trackLoadDetailsSuccessful() {
        tracker.track("load_pro_upgrade_details_successful")
    }

    fun trackLoadDetailsFailed() {
        tracker.track("load_pro_upgrade_details_failed")
    }

    fun trackUpgradeCancelled() {
        tracker.track("upgrade_status_cancelled")
    }

    fun trackUpgradeSuccessful() {
        tracker.track("upgrade_status_successful")
    }

    fun trackUpgradeFailed() {
        tracker.track("upgrade_status_successful")
    }

    fun trackGetDefaultFreePeriodClicked() {
        tracker.track(click("get_pro_default_free_period"))
    }

    fun trackGetAdRewardFreePeriodClicked() {
        tracker.track(click("get_pro_ad_reward_free_period"))
    }

    fun trackAdRewardFreePeriodDialogShown() {
        tracker.track("ad_reward_dialog_shown")
    }

    fun trackAdRewardFreePeriodAgreed() {
        tracker.track("ad_reward_free_period_agreed")
    }

    fun trackAdRewardFreePeriodCanceled() {
        tracker.track("ad_reward_free_period_cancelled")
    }

    fun trackDefaultFreePeriodActivated() {
        tracker.track("default_free_period_activated")
    }

    fun trackAdRewardFreePeriodActivated() {
        tracker.track("ad_reward_free_period_activated")
    }
}
