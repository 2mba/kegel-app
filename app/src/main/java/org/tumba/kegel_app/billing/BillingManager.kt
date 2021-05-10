package org.tumba.kegel_app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tumba.kegel_app.utils.IgnoreErrorHandler
import org.tumba.kegel_app.utils.asCoroutineExceptionHandler
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

    private var _isDetailsLoaded = false
    val isDetailsLoaded: Boolean
        get() = _isDetailsLoaded

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        Log.e("!!!!!!", billingResult.debugMessage)
        purchases?.let { _purchases.tryEmit(it) }
    }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    fun onAppStarted() {
        GlobalScope.launch(Dispatchers.Main + IgnoreErrorHandler.asCoroutineExceptionHandler()) {
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
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.e("!!!!", " onBillingSetupFinished ${billingResult.debugMessage}")
                    continuation.resume(billingResult)
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    Log.e("!!!!", " onBillingServiceDisconnected")
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
        purchasesResult.purchasesList?.let { _purchases.tryEmit(it) }

        Log.e("!!!!", " querySkuDetails ${skuDetailsResult.billingResult.debugMessage}")
        Log.e("!!!!", " querySkuDetails ${skuDetailsResult.skuDetailsList?.firstOrNull()?.description}")
        _isDetailsLoaded = true

        return purchasesResult.billingResult
    }

    fun startProUpgradePurchaseFlow(activity: Activity) {
        GlobalScope.launch(Dispatchers.Main + IgnoreErrorHandler.asCoroutineExceptionHandler()) {
            if (!billingClient.isReady) {
                connectAndQuerySkuDetails()
            }
            val proUpgradeSkuDetails = getSkuDetails(SKU_UPGRADE_PRO) ?: return@launch
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(proUpgradeSkuDetails)
                .build()
            val result = billingClient.launchBillingFlow(activity, flowParams)
            Log.e("!!!!", " startProUpgradePurchaseFlow ${result.debugMessage}  -> ${result.responseCode}")
        }
    }

    private suspend fun getSkuDetails(sku: String): SkuDetails? {
        return skuDetails.firstOrNull()?.firstOrNull { it.sku == sku }
    }

    companion object {
        const val SKU_UPGRADE_PRO = "pro_upgrade"
    }
}

