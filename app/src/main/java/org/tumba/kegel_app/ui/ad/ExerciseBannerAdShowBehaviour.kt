package org.tumba.kegel_app.ui.ad

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject


class ExerciseBannerAdShowBehaviour @Inject constructor(
    private val firebaseConfig: FirebaseRemoteConfig,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun canAdBeShown(): Boolean {
        val adsEnabled = firebaseConfig.getBoolean(ConfigConstants.adsEnabled)
        val exerciseBannerAdsEnabled = firebaseConfig.getBoolean(ConfigConstants.exerciseBannerAdsEnabled)
        if (!adsEnabled || !exerciseBannerAdsEnabled) {
            return false
        }
        val minExercisesNumber = firebaseConfig.getLong(ConfigConstants.exerciseBannerAdsMinExercisesNumber)
        return exerciseSettingsRepository.numberOfCompletedExercises.value >= minExercisesNumber
    }
}