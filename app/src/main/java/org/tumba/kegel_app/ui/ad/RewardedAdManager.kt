package org.tumba.kegel_app.ui.ad

import android.content.Context
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.tumba.kegel_app.analytics.AdsTracker
import org.tumba.kegel_app.config.AppBuildConfig
import org.tumba.kegel_app.utils.Event
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    context: Context,
    appBuildConfig: AppBuildConfig,
    adsTracker: AdsTracker
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val adWrapper = RewardedAdWrapper(context, appBuildConfig, adsTracker)

    val rewardedAdShowEvent: Flow<Event<RewardedAd?>> = adWrapper.rewardedAdShowEvent

    private val userEarnedRewardEvent = MutableSharedFlow<UserEarnedRewardEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun loadRewardAd() {
        scope.launch {
            adWrapper.loadRewardedAd()
        }
    }

    fun show(): Boolean {
        return adWrapper.show()
    }

    fun observeUserEarnedRewardEvent(): Flow<UserEarnedRewardEvent> = userEarnedRewardEvent

    fun onUserEarnedReward() {
        userEarnedRewardEvent.tryEmit(UserEarnedRewardEvent())
    }

    class UserEarnedRewardEvent
}

private class RewardedAdWrapper(
    private val context: Context,
    private val appBuildConfig: AppBuildConfig,
    private val adsTracker: AdsTracker
) {

    private var rewardedAd: RewardedAd? = null

    private val _rewardedAdShowEvent = MutableStateFlow<Event<RewardedAd?>>(Event(null))
    val rewardedAdShowEvent: Flow<Event<RewardedAd?>> = _rewardedAdShowEvent


    fun show(): Boolean {
        rewardedAd?.let { _rewardedAdShowEvent.value = Event(it) }
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                adsTracker.trackRewardedAdShown()
            }

            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd()
            }

            override fun onAdImpression() {
                adsTracker.trackRewardedAdImpression()
            }
        }
        return rewardedAd != null
    }

    fun loadRewardedAd() {
        if (rewardedAd != null) return
        RewardedAd.load(
            context.applicationContext,
            appBuildConfig.rewardedAdsUnitId,
            AdManagerAdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d("Reward: Failed to load ad ${adError.message}")
                    adsTracker.trackRewardedAdLoadFailed()
                    rewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Timber.d("Reward: Ad was loaded")
                    adsTracker.trackRewardedAdLoaded()
                    this@RewardedAdWrapper.rewardedAd = rewardedAd
                }
            }
        )
    }
}