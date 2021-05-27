package org.tumba.kegel_app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tumba.kegel_app.billing.ProUpgradeManager.Companion.SKU_UPGRADE_PRO
import org.tumba.kegel_app.utils.IgnoreErrorHandler
import org.tumba.kegel_app.utils.asCoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class BillingManager @Inject constructor(private val context: Context) {

    private val _skuDetails = MutableSharedFlow<List<SkuDetails>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val skuDetails: Flow<List<SkuDetails>> = _skuDetails
    private val _purchases = MutableSharedFlow<List<Purchase>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val purchases: Flow<List<Purchase>> = _purchases

    private val _billingResultUpdates = MutableSharedFlow<BillingResult>(replay = 0, extraBufferCapacity = 1)
    val billingResultUpdates: Flow<BillingResult> = _billingResultUpdates
    private var _isDetailsLoaded = false
    val isDetailsLoaded: Boolean
        get() = _isDetailsLoaded

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        Timber.d("purchasesUpdatedListener ${logPurchases(purchases)}")
        purchases?.let { _purchases.tryEmit(it) }
        _billingResultUpdates.tryEmit(billingResult)
        acknowledgePurchases(purchases ?: emptyList())
    }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    fun onAppStarted() {
        GlobalScope.launch {
            loadBillingData()
        }
    }

    suspend fun loadBillingData(): BillingResult {
        return withContext(Dispatchers.Main + IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            connectAndQuerySkuDetails()
        }
    }

    private suspend fun connectAndQuerySkuDetails(): BillingResult {
        val billingResult = connect()
        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            querySkuDetails()
        } else {
            billingResult
        }
    }

    private suspend fun connect(): BillingResult {
        return suspendCoroutine { continuation ->
            var isResumed = false
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(billingResult)
                    }
                }

                override fun onBillingServiceDisconnected() {
                }
            })
        }
    }

    private suspend fun querySkuDetails(): BillingResult {
        val skuList = listOf(SKU_UPGRADE_PRO)
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params)
        }
        skuDetailsResult.skuDetailsList?.let { _skuDetails.tryEmit(it) }

        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        }
        purchasesResult.purchasesList?.let { purchases ->
            _purchases.tryEmit(purchases)
            acknowledgePurchases(purchases)
        }

        Timber.d("querySkuDetails ${skuDetailsResult.billingResult.debugMessage}")
        Timber.d(
            "querySkuDetails list = %s, message = %s",
            skuDetailsResult.skuDetailsList?.firstOrNull()?.description,
            skuDetailsResult.billingResult.debugMessage
        )
        Timber.d("queryPurchases ${logPurchases(purchasesResult.purchasesList)}")
        _isDetailsLoaded = true

        return purchasesResult.billingResult
    }

    fun startPurchaseFlow(activity: Activity, sku: String) {
        GlobalScope.launch(Dispatchers.Main + IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            if (!billingClient.isReady) {
                connectAndQuerySkuDetails()
            }
            val proUpgradeSkuDetails = getSkuDetails(sku) ?: return@launch
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(proUpgradeSkuDetails)
                .build()
            val result = billingClient.launchBillingFlow(activity, flowParams)
            Timber.d("startPurchaseFlow ${result.debugMessage}  -> ${result.responseCode}")
        }
    }

    private fun acknowledgePurchases(purchases: List<Purchase>) {
        purchases.asSequence()
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .forEach { purchase ->
                Timber.d("acknowledgePurchases ${purchase.sku}")
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) { result ->
                    Timber.d("acknowledgePurchases ${result.debugMessage} -> ${result.responseCode}")
                }
            }
    }

    private suspend fun getSkuDetails(sku: String): SkuDetails? {
        return skuDetails.firstOrNull()?.firstOrNull { it.sku == sku }
    }

    private fun logPurchases(purchases: List<Purchase>?): String? {
        return purchases?.joinToString { "${it.sku}, state = ${it.purchaseState}" }
    }
}
