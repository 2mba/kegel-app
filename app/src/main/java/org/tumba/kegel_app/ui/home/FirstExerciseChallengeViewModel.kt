package org.tumba.kegel_app.ui.home

import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragmentDirections.Companion.actionFirstExerciseChallengeDialogFragmentToScreenExercise
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragmentDirections.Companion.actionFirstExerciseChallengeDialogFragmentToScreenExerciseInfoFragment
import javax.inject.Inject

class FirstExerciseChallengeViewModel @Inject constructor(
) : BaseViewModel() {

    fun onClickShowExerciseInfo() {
        navigate(actionFirstExerciseChallengeDialogFragmentToScreenExerciseInfoFragment())
    }

    fun onClickGoToExercise() {
        navigate(actionFirstExerciseChallengeDialogFragmentToScreenExercise())
    }
}
