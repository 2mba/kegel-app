package org.tumba.kegel_app.data

data class ExerciseConfig(
    val preparationDuration: Time,
    val holdingDuration: Time,
    val relaxDuration: Time,
    val repeats: Int
)