package org.tumba.kegel_app.ui.home

import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.ProUpgradeTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.ui.ad.RewardedAdManager
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.showSnackbar
import javax.inject.Inject

class AdRewardProUpgradeViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val proUpgradeManager: ProUpgradeManager,
    private val rewardedAdManager: RewardedAdManager,
    private val tracker: ProUpgradeTracker,
    remoteConfig: FirebaseRemoteConfig
) : BaseViewModel() {

    var isCloseProUpgradeDialog = false
    val freePeriodDays = remoteConfig[ConfigConstants.adRewardedFreePeriodDays].asLong().toInt()

    init {
        tracker.trackAdRewardFreePeriodDialogShown()
    }

    fun onClickDone() {
        tracker.trackGetDefaultFreePeriodClicked()
        viewModelScope.launch {
            showAdAndActivateFreePeriod()
        }
    }

    fun onClickCancel() {
        tracker.trackAdRewardFreePeriodCanceled()
        back()
    }

    private fun showAdAndActivateFreePeriod() {
        viewModelScope.launch {
            if (rewardedAdManager.show()) {
                rewardedAdManager.observeUserEarnedRewardEvent()
                    .take(1)
                    .collect { activateFreePeriod() }
            } else {
                activateFreePeriod()
            }
        }
    }

    private fun activateFreePeriod() {
        viewModelScope.launch {
            tracker.trackAdRewardFreePeriodAgreed()
            proUpgradeManager.activateAdRewardFreePeriod()
            showSnackbar(
                resourceProvider.getString(
                    R.string.screen_ad_reward_pro_upgrade_toast_success,
                    freePeriodDays.toString()
                )
            )
            closeProUpgradeDialog()
        }
    }

    private fun closeProUpgradeDialog() {
        if (isCloseProUpgradeDialog) {
            navigate(AdRewardProUpgradeDialogFragmentDirections.actionCloseProUpgradeScreen())
        } else {
            back()
        }
    }
}
