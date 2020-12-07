package org.tumba.kegel_app.di.component

import dagger.Component
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.di.module.AppModule
import org.tumba.kegel_app.di.module.DomainModule
import org.tumba.kegel_app.di.module.PresentationModule
import org.tumba.kegel_app.di.module.ViewModelModule
import org.tumba.kegel_app.service.ExerciseAndroidService
import org.tumba.kegel_app.ui.exercise.ExerciseFragment
import org.tumba.kegel_app.ui.exercise.ExerciseResultFragment
import org.tumba.kegel_app.ui.exercise.ExerciseResultViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseViewModel
import org.tumba.kegel_app.ui.home.HomeFragment
import org.tumba.kegel_app.ui.home.HomeViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DomainModule::class, PresentationModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(obj: MainActivity)

    fun inject(obj: ExerciseFragment)

    fun inject(obj: HomeFragment)

    fun inject(obj: ExerciseAndroidService)

    fun inject(obj: ExerciseResultFragment)

    fun getExerciseViewModel(): ExerciseViewModel

    fun getExerciseResultViewModel(): ExerciseResultViewModel

    fun getHomeViewModel(): HomeViewModel
}