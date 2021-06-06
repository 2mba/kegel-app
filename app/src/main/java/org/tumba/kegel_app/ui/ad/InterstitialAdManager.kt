package org.tumba.kegel_app.ui.ad

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.AdsTracker
import org.tumba.kegel_app.config.AppBuildConfig
import org.tumba.kegel_app.utils.Event
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor(
    context: Context,
    appBuildConfig: AppBuildConfig,
    private val interstitialAdShowBehaviour: InterstitialAdShowBehaviour,
    private val adsTracker: AdsTracker
) : NavController.OnDestinationChangedListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val adWrapper = InterstitialAdWrapper(context, appBuildConfig, adsTracker)

    val interstitialAdShowEvent: Flow<Event<InterstitialAd?>> = adWrapper.interstitialAdShowEvent

    private var lastDestination: NavDestination? = null


    init {
        adWrapper.loadInterstitialAd()
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (isNavigatedFromExercise(destination)) {
            scope.launch {
                if (interstitialAdShowBehaviour.canAdBeShown()) {
                    delay(INTERSTITIAL_ADD_SHOW_DELAY_MILLIS)
                    adWrapper.show()
                }
            }
        }
        lastDestination = destination
    }

    private fun isNavigatedFromExercise(destination: NavDestination): Boolean {
        return destination.id == R.id.screenHome && lastDestination?.id == R.id.screenExercise
    }

    companion object {
        private const val INTERSTITIAL_ADD_SHOW_DELAY_MILLIS = 500L
    }
}

private class InterstitialAdWrapper(
    private val context: Context,
    private val appBuildConfig: AppBuildConfig,
    private val adsTracker: AdsTracker
) {

    private var interstitialAd: InterstitialAd? = null

    private val _interstitialAdShowEvent = MutableStateFlow<Event<InterstitialAd?>>(Event(null))
    val interstitialAdShowEvent: Flow<Event<InterstitialAd?>> = _interstitialAdShowEvent


    fun show() {
        interstitialAd?.let { _interstitialAdShowEvent.value = Event(it) }
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                adsTracker.trackInterstitialAdShown()
            }
        }
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context.applicationContext,
            appBuildConfig.interstitialAdsUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d("Failed to load ad ${adError.message}")
                    adsTracker.trackInterstitialAdLoadFailed()
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Timber.d("Ad was loaded")
                    adsTracker.trackInterstitialAdLoaded()
                    this@InterstitialAdWrapper.interstitialAd = interstitialAd
                }
            })
    }
}