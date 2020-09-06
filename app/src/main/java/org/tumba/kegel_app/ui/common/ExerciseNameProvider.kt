package org.tumba.kegel_app.ui.common

import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.utils.Empty
import javax.inject.Inject

class ExerciseNameProvider @Inject constructor(private val resourceProvider: ResourceProvider) {

    fun exerciseName(state: ExerciseState): String {
        return when (state) {
            is ExerciseState.Preparation -> R.string.screen_exercise_exercise_preparation
            is ExerciseState.Holding -> R.string.screen_exercise_exercise_holding
            is ExerciseState.Relax -> R.string.screen_exercise_exercise_relax
            is ExerciseState.Pause -> R.string.screen_exercise_exercise_paused
            is ExerciseState.Finish -> R.string.screen_exercise_exercise_paused
            ExerciseState.NotStarted -> null
        }
            .let { nameRes ->
                if (nameRes != null) {
                    resourceProvider.getString(nameRes)
                } else {
                    String.Empty
                }
            }
    }
}