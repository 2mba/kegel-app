package org.tumba.kegel_app.utils

val String.Companion.Empty: String
    get() = ""

private const val EXERCISE_TIME_FORMAT = "%02d:%02d"

fun formatExerciseDuration(seconds: Long): String {
    val sec = seconds % 60
    val min = seconds / 60
    return String.format(EXERCISE_TIME_FORMAT, min, sec)
}