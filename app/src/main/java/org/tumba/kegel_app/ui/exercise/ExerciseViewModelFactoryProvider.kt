package org.tumba.kegel_app.ui.exercise

import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.analytics.ExerciseTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.core.system.PermissionProvider
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseServiceInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.ad.ExerciseBannerAdShowBehaviour
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import org.tumba.kegel_app.ui.utils.ProviderViewModelFactory
import javax.inject.Inject

class ExerciseViewModelFactoryProvider @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseServiceInteractor: ExerciseServiceInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseParametersProvider: ExerciseParametersProvider,
    private val exerciseNameProvider: ExerciseNameProvider,
    private val proUpgradeManager: ProUpgradeManager,
    private val tracker: ExerciseTracker,
    private val resourceProvider: ResourceProvider,
    private val permissionProvider: PermissionProvider,
    private val exerciseBannerAdShowBehaviour: ExerciseBannerAdShowBehaviour
) {

    fun build(exerciseType: ExerciseType): ViewModelProvider.Factory {
        return ProviderViewModelFactory {
            ExerciseViewModel(
                exerciseType = exerciseType,
                exerciseInteractor = exerciseInteractor,
                exerciseServiceInteractor = exerciseServiceInteractor,
                exerciseSettingsRepository = exerciseSettingsRepository,
                exerciseParametersProvider = exerciseParametersProvider,
                exerciseNameProvider = exerciseNameProvider,
                proUpgradeManager = proUpgradeManager,
                tracker = tracker,
                resourceProvider = resourceProvider,
                permissionProvider = permissionProvider,
                exerciseBannerAdShowBehaviour = exerciseBannerAdShowBehaviour,
            )
        }
    }
}