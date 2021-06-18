package org.tumba.kegel_app.domain

data class ExerciseConfig(
    val preparationDuration: Time,
    val holdingDuration: Time,
    val relaxDuration: Time,
    val repeats: Int,
    val isPredefined: Boolean
)