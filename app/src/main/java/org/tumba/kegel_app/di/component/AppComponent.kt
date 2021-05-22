package org.tumba.kegel_app.di.component

import dagger.Component
import org.tumba.kegel_app.KegelApplication
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
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragment
import org.tumba.kegel_app.ui.home.HomeFragment
import org.tumba.kegel_app.ui.home.HomeViewModel
import org.tumba.kegel_app.ui.proupgrade.ProUpgradeFragment
import org.tumba.kegel_app.ui.settings.AboutAppFragment
import org.tumba.kegel_app.ui.settings.SettingsFragment
import org.tumba.kegel_app.worker.ReminderWorker
import org.tumba.vacuum_app.ui.home.FirstEntryDialogFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DomainModule::class, PresentationModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(obj: KegelApplication)

    fun inject(obj: MainActivity)

    fun inject(obj: ExerciseFragment)

    fun inject(obj: HomeFragment)

    fun inject(obj: SettingsFragment)

    fun inject(obj: ExerciseAndroidService)

    fun inject(obj: ExerciseResultFragment)

    fun inject(obj: ReminderWorker)

    fun inject(obj: FirstExerciseChallengeDialogFragment)

    fun inject(obj: ProUpgradeFragment)

    fun inject(obj: FirstEntryDialogFragment)

    fun inject(obj: AboutAppFragment)

    fun getExerciseViewModel(): ExerciseViewModel

    fun getExerciseResultViewModel(): ExerciseResultViewModel

    fun getHomeViewModel(): HomeViewModel
}