package org.tumba.kegel_app.ui.proupgrade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.ProUpgradeTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.ui.ad.RewardedAdManager
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.showSnackbar
import org.tumba.kegel_app.utils.Event
import org.tumba.kegel_app.utils.IgnoreErrorHandler
import org.tumba.kegel_app.utils.asCoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject

class ProUpgradeViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val proUpgradeManager: ProUpgradeManager,
    private val tracker: ProUpgradeTracker,
    rewardedAdManager: RewardedAdManager,
    remoteConfig: FirebaseRemoteConfig
) : BaseViewModel() {

    val price: LiveData<String?> = proUpgradeManager.proUpgradeSkuDetails
        .map { details ->
            if (details != null) {
                "${details.price} ${details.priceCurrencyCode}"
            } else {
                null
            }
        }
        .asLiveData()

    val isTryProUpgradeButtonVisible =
        proUpgradeManager.defaultFreePeriodState.value == ProUpgradeManager.FreePeriodState.NotActivated
    val isAdRewardButtonVisible = combine(
        proUpgradeManager.defaultFreePeriodState,
        proUpgradeManager.adAwardFreePeriodState
    ) { defaultFreePeriodState, adAwardFreePeriodState ->
        defaultFreePeriodState is ProUpgradeManager.FreePeriodState.Expired &&
                adAwardFreePeriodState != ProUpgradeManager.FreePeriodState.Active
    }.asLiveData()
    val freePeriodDays = remoteConfig[ConfigConstants.adRewardedFreePeriodDays].asLong().toInt()
    val startProPurchasingFlow = MutableLiveData(Event(false))
    val isLoading = MutableLiveData(false)

    init {
        loadBillingDetails()
        rewardedAdManager.loadRewardAd()
    }

    fun onUpgradeToProClicked() {
        tracker.trackClickBuy()
        startProPurchasingFlow.value = Event(true)
        handlePurchase()
    }

    fun onRestorePurchaseClicked() {
        tracker.trackClickRestorePurchase()
        viewModelScope.launch(IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            try {
                isLoading.value = true
                val result = proUpgradeManager.restorePurchases()
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    tracker.trackLoadRestorePurchaseSuccessful()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_message_purchases_restored))
                    back()
                } else {
                    tracker.trackRestorePurchaseFailed()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_error_failed_restore_purchases))
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    fun onGetFreePeriodClicked() {
        viewModelScope.launch {
            tracker.trackGetDefaultFreePeriodClicked()
            proUpgradeManager.activateDefaultFreePeriod()
            showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_message_free_period_activated))
            back()
        }
    }

    fun onGetAdRewardFreePeriodClicked() {
        viewModelScope.launch {
            tracker.trackGetAdRewardFreePeriodClicked()
            navigate(
                ProUpgradeFragmentDirections.actionScreenProUpgradeToAdRewardProUpgradeDialogFragment(
                    isCloseProUpgradeScreen = true
                )
            )
        }
    }

    fun onClickClose() {
        tracker.trackClose()
        navigate(
            ProUpgradeFragmentDirections.actionCloseScreenToAdRewardProUpgradeDialogFragment(
                isCloseProUpgradeScreen = false
            )
        )
    }

    private fun loadBillingDetails() {
        viewModelScope.launch(IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            val result = proUpgradeManager.loadBillingDetails()
            when (result?.responseCode) {
                null, BillingClient.BillingResponseCode.OK -> {
                    // ok, do nothing
                    tracker.trackLoadDetailsSuccessful()
                }
                else -> {
                    tracker.trackLoadDetailsFailed()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_error_something_went_wrong))
                }
            }
        }
    }

    private fun handlePurchase() {
        viewModelScope.launch(IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            val result = proUpgradeManager.awaitPurchaseResult()
            Timber.d("handlePurchase ${result?.debugMessage}, code = ${result?.responseCode}")
            when (result?.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    tracker.trackUpgradeSuccessful()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_message_upgraded_successfully))
                    back()
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    tracker.trackUpgradeCancelled()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_error_upgrade_cancelled))
                }
                else -> {
                    tracker.trackUpgradeFailed()
                    showSnackbar(resourceProvider.getString(R.string.screen_pro_upgrade_error_failed_upgrade))
                }
            }
        }
    }
}

