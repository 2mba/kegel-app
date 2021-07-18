package org.tumba.kegel_app.di.module

import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.db.AppDatabase
import org.tumba.kegel_app.db.CompletedExerciseDao
import javax.inject.Singleton

@Module
class DatabaseModule(
    private val database: AppDatabase
) {

    @Provides
    fun provideAppDatabase(): AppDatabase = database

    @Singleton
    @Provides
    fun provideCompletedExerciseDao(): CompletedExerciseDao = database.completedExerciseDao()
}