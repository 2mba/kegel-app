package org.tumba.kegel_app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_exercise")
class CompletedExerciseEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "completed_at") val completedAt: Long,
    @ColumnInfo(name = "is_custom_exercise") val isCustomExercise: Boolean
)
