package org.tumba.kegel_app.ui.proupgrade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.ProUpgradeTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.core.system.ResourceProvider
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
    private val tracker: ProUpgradeTracker

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

    val startProPurchasingFlow = MutableLiveData(Event(false))
    val isLoading = MutableLiveData(false)

    init {
        loadBillingDetails()
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

    fun onClickClose() {
        tracker.trackClose()
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

