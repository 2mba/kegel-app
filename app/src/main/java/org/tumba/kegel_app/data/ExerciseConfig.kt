package org.tumba.kegel_app.data

import org.tumba.kegel_app.data.Time

data class ExerciseConfig(
    val preparationDuration: Time,
    val holdingDuration: Time,
    val relaxDuration: Time,
    val repeats: Int
)