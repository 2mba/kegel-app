package org.tumba.kegel_app.domain

import org.tumba.kegel_app.service.ExerciseService

class ExerciseServiceInteractor(
    private val exerciseService: ExerciseService
) {

    fun startService() {
        exerciseService.startService()
    }

    fun stopService() {
        exerciseService.stopService()
    }
}