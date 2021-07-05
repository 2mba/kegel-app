package org.tumba.kegel_app.billing

import android.app.Activity
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.tumba.kegel_app.analytics.ProUpgradeTracker
import org.tumba.kegel_app.analytics.UserPropertyTracker
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.repository.FreePeriodSettings
import org.tumba.kegel_app.utils.DateHelper
import org.tumba.kegel_app.utils.IgnoreErrorHandler
import org.tumba.kegel_app.utils.asCoroutineExceptionHandler
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
@Singleton
class ProUpgradeManager @Inject constructor(
    private val billingManager: BillingManager,
    private val settingsRepository: ExerciseSettingsRepository,
    private val userPropertyTracker: UserPropertyTracker,
    private val dateHelper: DateHelper,
    private val firebaseConfig: FirebaseRemoteConfig,
    private val proUpgradeTracker: ProUpgradeTracker
) {

    private val scope = MainScope()

    @Suppress("USELESS_CAST")
    private val purchases = billingManager.purchases
        .map { it as List<Purchase>? }
        .onStart { emit(null) }
    val isProAvailable: StateFlow<Boolean> = combine(
        isFreePeriodActive(),
        purchases
    ) { isFreePeriodActive, purchases ->
        updateProAvailabilityInSettings(purchases)
        val isProPurchased = isProPurchased(purchases)
        val isProAvailableFromSettings = (settingsRepository.isProAvailable.value && isProPurchased == null)
        trackProPurchased(isProPurchased)
        isProAvailableFromSettings || isProPurchased == true || isFreePeriodActive
    }.stateIn(scope + IgnoreErrorHandler.asCoroutineExceptionHandler(), SharingStarted.Eagerly, false)

    val isProPurchased: StateFlow<Boolean> = purchases.map { purchases ->
        updateProAvailabilityInSettings(purchases)
        val isProPurchased = isProPurchased(purchases)
        val isProAvailableFromSettings = (settingsRepository.isProAvailable.value && isProPurchased == null)
        trackProPurchased(isProPurchased)
        isProAvailableFromSettings || isProPurchased == true
    }.stateIn(scope + IgnoreErrorHandler.asCoroutineExceptionHandler(), SharingStarted.Eagerly, false)

    val proUpgradeSkuDetails = billingManager.skuDetails
        .map { details -> details.firstOrNull { it.sku == SKU_UPGRADE_PRO } }

    val defaultFreePeriodState = getFreePeriodStateStateFlow(settingsRepository.defaultFreePeriod)
    val adAwardFreePeriodState = getFreePeriodStateStateFlow(settingsRepository.adRewardFreePeriod)

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

    suspend fun activateDefaultFreePeriod() {
        withContext(Dispatchers.Default) {
            if (defaultFreePeriodState.value == FreePeriodState.NotActivated) {
                proUpgradeTracker.trackDefaultFreePeriodActivated()
                activateFreePeriod(
                    freePeriod = settingsRepository.defaultFreePeriod,
                    days = firebaseConfig[ConfigConstants.defaultFreePeriodDays].asLong().toInt()
                )
            }
        }
    }

    suspend fun activateAdRewardFreePeriod() {
        withContext(Dispatchers.Default) {
            if (adAwardFreePeriodState.value != FreePeriodState.Active) {
                proUpgradeTracker.trackAdRewardFreePeriodActivated()
                activateFreePeriod(
                    freePeriod = settingsRepository.adRewardFreePeriod,
                    days = firebaseConfig[ConfigConstants.adRewardedFreePeriodDays].asLong().toInt()
                )
                settingsRepository.adRewardFreePeriod.isExpirationShown.value = false
            }
        }
    }

    suspend fun setDefaultFreePeriodExpirationShown() {
        withContext(Dispatchers.Default) {
            settingsRepository.defaultFreePeriod.isExpirationShown.value = true
        }
    }

    suspend fun setAdRewardFreePeriodExpirationShown() {
        withContext(Dispatchers.Default) {
            settingsRepository.adRewardFreePeriod.isExpirationShown.value = true
        }
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

    private fun isFreePeriodActive(): Flow<Boolean> {
        return combine(
            isFreePeriodActive(settingsRepository.defaultFreePeriod),
            isFreePeriodActive(settingsRepository.adRewardFreePeriod)
        ) { isDefaultFreePeriod, isAdRewardFreePeriod -> isDefaultFreePeriod || isAdRewardFreePeriod }
    }

    private fun isFreePeriodActive(freePeriod: FreePeriodSettings): Flow<Boolean> {
        return tickerFlow()
            .flatMapLatest { getFreePeriodState(freePeriod) }
            .map { it == FreePeriodState.Active }
    }

    private fun getFreePeriodStateStateFlow(freePeriod: FreePeriodSettings): StateFlow<FreePeriodState> {
        return tickerFlow().flatMapLatest { getFreePeriodState(freePeriod) }
            .stateIn(
                scope + IgnoreErrorHandler.asCoroutineExceptionHandler(),
                SharingStarted.Eagerly,
                FreePeriodState.NotActivated
            )
    }

    private fun getFreePeriodState(freePeriod: FreePeriodSettings): Flow<FreePeriodState> {
        return combine(freePeriod.startDate.asFlow(), freePeriod.isExpirationShown.asFlow()) { _, _ ->
            if (freePeriod.startDate.value == 0L) {
                FreePeriodState.NotActivated
            } else {
                val endDate = Calendar.getInstance().apply {
                    time = Date(freePeriod.startDate.value)
                    add(Calendar.DAY_OF_YEAR, freePeriod.days.value)
                    Timber.d("Free period expiration date ${SimpleDateFormat.getInstance().format(this.time)}")
                    // add(Calendar.MINUTE, 2)
                }
                if (endDate.after(dateHelper.now())) {
                    FreePeriodState.Active
                } else {
                    FreePeriodState.Expired(isExpirationShown = freePeriod.isExpirationShown.value)
                }
            }
        }
    }

    private fun activateFreePeriod(freePeriod: FreePeriodSettings, days: Int) {
        freePeriod.startDate.value = dateHelper.now().time.time
        freePeriod.days.value = days
        freePeriod.isExpirationShown.value = false
    }

    private fun trackProPurchased(isProPurchased: Boolean?) {
        if (isProPurchased != null) {
            userPropertyTracker.setProPurchased(isProPurchased)
        }
    }

    private fun tickerFlow(): Flow<Int> {
        return flow {
            while (currentCoroutineContext().isActive) {
                emit(0)
                delay(TICKER_FLOW_DELAY)
            }
        }
    }

    companion object {
        const val SKU_UPGRADE_PRO = "pro_upgrade"
        private val TICKER_FLOW_DELAY = 1.minutes
    }

    sealed class FreePeriodState {
        object NotActivated : FreePeriodState()
        object Active : FreePeriodState()
        class Expired(val isExpirationShown: Boolean) : FreePeriodState()
    }
}