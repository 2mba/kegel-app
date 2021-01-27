package org.tumba.kegel_app.ui.exercise

import org.tumba.kegel_app.domain.interactor.ExerciseServiceInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class ExerciseResultViewModel @Inject constructor(
    private val serviceInteractor: ExerciseServiceInteractor,
    private val settingsRepository: ExerciseSettingsRepository
) : BaseViewModel() {

    fun onDismiss() {
        if (settingsRepository.isNotificationEnabled()) {
            serviceInteractor.clearNotification()
        }
    }
}