package org.tumba.kegel_app.ui.home.dialog

import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseType
import javax.inject.Inject

class FirstExerciseChallengeViewModel @Inject constructor(
) : BaseViewModel() {

    var exerciseType: ExerciseType? = null

    fun onClickShowExerciseInfo() {
        exerciseType?.let { exerciseType ->
            navigate(
                FirstExerciseChallengeDialogFragmentDirections.actionFirstExerciseChallengeDialogFragmentToScreenExerciseInfoFragment(
                    showExerciseButton = true,
                    type = exerciseType
                )
            )
        }

    }

    fun onClickGoToExercise() {
        exerciseType?.let { exerciseType ->
            navigate(FirstExerciseChallengeDialogFragmentDirections.actionFirstExerciseChallengeDialogFragmentToScreenExercise(exerciseType))
        }
    }
}
