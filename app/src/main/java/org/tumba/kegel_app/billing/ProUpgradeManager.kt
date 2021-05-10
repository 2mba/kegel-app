package org.tumba.kegel_app.billing

import com.android.billingclient.api.Purchase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProUpgradeManager @Inject constructor(
    billingManager: BillingManager,
    private val settingsRepository: ExerciseSettingsRepository
) {

    @Suppress("USELESS_CAST")
    private val purchases = billingManager.purchases
        .map { it as List<Purchase>? }
        .onStart { emit(null) }
    val isProAvailable: Flow<Boolean> = combine(
        purchases,
        settingsRepository.isProAvailable.asFlow().take(1)
    ) { purchases, isProAvailable ->
        updateProAvailabilityInSettings(purchases)
        val isProPurchased = isProPurchased(purchases)
        val isProAvailableFromSettings = (isProAvailable && isProPurchased == null)
        isProAvailableFromSettings || isProPurchased == true
    }.shareIn(GlobalScope, SharingStarted.Eagerly, replay = 1)

    private fun isProPurchased(purchases: List<Purchase>?): Boolean? {
        return if (purchases != null) {
            purchases.firstOrNull { it.sku == BillingManager.SKU_UPGRADE_PRO && it.isAcknowledged } != null
        } else {
            null
        }
    }

    private fun updateProAvailabilityInSettings(purchases: List<Purchase>?) {
        if (purchases != null) {
            settingsRepository.isProAvailable.value = isProPurchased(purchases) == true
        }
    }
}