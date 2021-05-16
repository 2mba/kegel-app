package org.tumba.kegel_app.billing

import android.app.Activity
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.utils.IgnoreErrorHandler
import org.tumba.kegel_app.utils.asCoroutineExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProUpgradeManager @Inject constructor(
    private val billingManager: BillingManager,
    private val settingsRepository: ExerciseSettingsRepository
) {

    @Suppress("USELESS_CAST")
    private val purchases = billingManager.purchases
        .map { it as List<Purchase>? }
        .onStart { emit(null) }
    val isProAvailable: Flow<Boolean> = purchases.map { purchases ->
        updateProAvailabilityInSettings(purchases)
        val isProPurchased = isProPurchased(purchases)
        val isProAvailableFromSettings = (settingsRepository.isProAvailable.value && isProPurchased == null)
        isProAvailableFromSettings || isProPurchased == true
    }.shareIn(GlobalScope + IgnoreErrorHandler.asCoroutineExceptionHandler(), SharingStarted.Eagerly, replay = 1)

    val proUpgradeSkuDetails = billingManager.skuDetails
        .map { details -> details.firstOrNull { it.sku == SKU_UPGRADE_PRO } }

    suspend fun loadBillingDetails(): BillingResult? {
        return if (billingManager.isDetailsLoaded) {
            null
        } else {
            billingManager.loadBillingData()
        }
    }

    fun startProUpgradePurchaseFlow(activity: Activity) {
        billingManager.startPurchaseFlow(activity, SKU_UPGRADE_PRO)
    }

    suspend fun awaitPurchaseResult(): BillingResult? {
        return billingManager.billingResultUpdates.firstOrNull()
    }

    suspend fun restorePurchases(): BillingResult {
        return billingManager.loadBillingData()
    }

    private fun isProPurchased(purchases: List<Purchase>?): Boolean? {
        return if (purchases != null) {
            purchases.firstOrNull { purchase ->
                purchase.sku == SKU_UPGRADE_PRO &&
                        (purchase.purchaseState == Purchase.PurchaseState.PURCHASED ||
                                purchase.purchaseState == Purchase.PurchaseState.PENDING)
            } != null
        } else {
            null
        }
    }

    private fun updateProAvailabilityInSettings(purchases: List<Purchase>?) {
        if (purchases != null) {
            val isProPurchased = isProPurchased(purchases) == true
            if (settingsRepository.isProAvailable.value != isProPurchased) {
                settingsRepository.isProAvailable.value = isProPurchased
            }
        }
    }

    companion object {
        const val SKU_UPGRADE_PRO = "pro_upgrade"
    }
}