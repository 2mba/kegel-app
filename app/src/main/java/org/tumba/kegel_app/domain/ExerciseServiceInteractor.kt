package org.tumba.kegel_app.domain

import org.tumba.kegel_app.service.ExerciseService
import javax.inject.Inject

class ExerciseServiceInteractor @Inject constructor(
    private val exerciseService: ExerciseService
) {

    fun startService() {
        exerciseService.startService()
    }

    fun stopService() {
        exerciseService.stopService()
    }

    fun clearNotification() {
        exerciseService.clearNotification()
    }
}