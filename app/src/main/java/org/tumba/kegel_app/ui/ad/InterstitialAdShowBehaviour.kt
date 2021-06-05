package org.tumba.kegel_app.ui.ad

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject


class InterstitialAdShowBehaviour @Inject constructor(
    private val firebaseConfig: FirebaseRemoteConfig,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun canAdBeShown(): Boolean {
        val adsEnabled = firebaseConfig.getBoolean(ConfigConstants.adsEnabled)
        val interstitialAdsEnabled = firebaseConfig.getBoolean(ConfigConstants.interstitialAdsEnabled)
        if (!adsEnabled || !interstitialAdsEnabled) {
            return false
        }
        val minExercisesNumber = firebaseConfig.getLong(ConfigConstants.interstitialAdsMinExercisesNumber)
        val frequency = firebaseConfig.getLong(ConfigConstants.interstitialAdsFrequency)

        val exercisesAfterThreshold = exerciseSettingsRepository.numberOfCompletedExercises.value - minExercisesNumber
        return exercisesAfterThreshold >= 0 && exercisesAfterThreshold % frequency == 0L
    }
}