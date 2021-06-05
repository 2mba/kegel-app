package org.tumba.kegel_app.ui.ad

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.util.TestPreference

class InterstitialAdShowBehaviourTest {

    @RelaxedMockK
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    @RelaxedMockK
    private lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    private lateinit var interstitialAdShowBehaviour: InterstitialAdShowBehaviour


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        interstitialAdShowBehaviour = InterstitialAdShowBehaviour(firebaseRemoteConfig, exerciseSettingsRepository)
    }

    @Test
    fun `should not show ad if number of exercises lower then minimum from config`() {
        // Arrange
        initConfig(
            adsEnabled = true,
            interstitialAdsEnabled = true,
            interstitialAdsMinExercisesNumber = 10,
            interstitialAdsFrequency = 3
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(3)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` false
    }

    @Test
    fun `should show ad if number of exercises equal to minimum from config`() {
        // Arrange
        initConfig(
            adsEnabled = true,
            interstitialAdsEnabled = true,
            interstitialAdsMinExercisesNumber = 10,
            interstitialAdsFrequency = 3
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(10)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` true
    }

    @Test
    fun `should not show ad if frequency does not match`() {
        // Arrange
        initConfig(
            adsEnabled = true,
            interstitialAdsEnabled = true,
            interstitialAdsMinExercisesNumber = 10,
            interstitialAdsFrequency = 3
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(12)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` false
    }

    @Test
    fun `should not show ad if frequency matches`() {
        // Arrange
        initConfig(
            adsEnabled = true,
            interstitialAdsEnabled = true,
            interstitialAdsMinExercisesNumber = 10,
            interstitialAdsFrequency = 3
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(13)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` true
    }

    @Test
    fun `should not show ad if ads disabled`() {
        // Arrange
        initConfig(
            adsEnabled = false,
            interstitialAdsEnabled = true,
            interstitialAdsMinExercisesNumber = 10,
            interstitialAdsFrequency = 3
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(10)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` false
    }

    @Test
    fun `should not show ad if interstitial ads disabled`() {
        // Arrange
        initConfig(
            adsEnabled = true,
            interstitialAdsEnabled = false,
            interstitialAdsMinExercisesNumber = 3,
            interstitialAdsFrequency = 1
        )
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns TestPreference(3)
        // Act
        val canAdBeShown = interstitialAdShowBehaviour.canAdBeShown()
        // Assert
        canAdBeShown `should be equal to` false
    }

    private fun initConfig(
        adsEnabled: Boolean,
        interstitialAdsEnabled: Boolean,
        interstitialAdsMinExercisesNumber: Long,
        interstitialAdsFrequency: Long
    ) {
        every { firebaseRemoteConfig.getBoolean(ConfigConstants.adsEnabled) } returns adsEnabled
        every { firebaseRemoteConfig.getBoolean(ConfigConstants.interstitialAdsEnabled) } returns interstitialAdsEnabled
        every { firebaseRemoteConfig.getLong(ConfigConstants.interstitialAdsMinExercisesNumber) } returns interstitialAdsMinExercisesNumber
        every { firebaseRemoteConfig.getLong(ConfigConstants.interstitialAdsFrequency) } returns interstitialAdsFrequency
    }
}