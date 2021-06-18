package org.tumba.kegel_app.ui.home

import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseType
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragmentDirections.Companion.actionFirstExerciseChallengeDialogFragmentToScreenExercise
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragmentDirections.Companion.actionFirstExerciseChallengeDialogFragmentToScreenExerciseInfoFragment
import javax.inject.Inject

class FirstExerciseChallengeViewModel @Inject constructor(
) : BaseViewModel() {

    var exerciseType: ExerciseType? = null

    fun onClickShowExerciseInfo() {
        exerciseType?.let { exerciseType ->
            navigate(
                actionFirstExerciseChallengeDialogFragmentToScreenExerciseInfoFragment(
                    showExerciseButton = true,
                    type = exerciseType
                )
            )
        }

    }

    fun onClickGoToExercise() {
        exerciseType?.let { exerciseType ->
            navigate(actionFirstExerciseChallengeDialogFragmentToScreenExercise(exerciseType))
        }
    }
}
