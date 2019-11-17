package org.tumba.kegel_app.exercise.domain.entity

data class ExerciseConfig(
    val preparationDuration: Time,
    val holdingDuration: Time,
    val relaxDuration: Time,
    val repeats: Int
)