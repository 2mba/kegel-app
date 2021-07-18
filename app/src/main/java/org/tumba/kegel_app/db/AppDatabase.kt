package org.tumba.kegel_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CompletedExerciseEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun completedExerciseDao(): CompletedExerciseDao
}

fun buildDatabase(applicationContext: Context): AppDatabase {
    return Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "app-database"
    ).build()
}
