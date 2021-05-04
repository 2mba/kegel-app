package org.tumba.kegel_app.ui.exercise

import org.tumba.kegel_app.R

enum class ExerciseBackgroundMode {
    NONE,
    NOTIFICATION,
    FLOATING_VIEW
}

fun getExerciseBackgroundModeStringsMap(): Map<ExerciseBackgroundMode, Int> {
    return mapOf(
        ExerciseBackgroundMode.NONE to R.string.screen_exercise_background_mode_none,
        ExerciseBackgroundMode.NOTIFICATION to R.string.screen_exercise_background_mode_notification,
        ExerciseBackgroundMode.FLOATING_VIEW to R.string.screen_exercise_background_mode_floating_view
    )
}