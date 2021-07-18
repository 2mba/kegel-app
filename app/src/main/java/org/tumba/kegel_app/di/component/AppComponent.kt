package org.tumba.kegel_app.di.component

import dagger.Component
import org.tumba.kegel_app.KegelApplication
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.di.module.*
import org.tumba.kegel_app.service.ExerciseAndroidService
import org.tumba.kegel_app.ui.customexercise.CustomExerciseSetupFragment
import org.tumba.kegel_app.ui.exercise.ExerciseFragment
import org.tumba.kegel_app.ui.exercise.ExerciseResultFragment
import org.tumba.kegel_app.ui.home.AdRewardProUpgradeDialogFragment
import org.tumba.kegel_app.ui.home.FirstEntryDialogFragment
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeDialogFragment
import org.tumba.kegel_app.ui.home.HomeFragment
import org.tumba.kegel_app.ui.proupgrade.ProUpgradeFragment
import org.tumba.kegel_app.ui.settings.AboutAppFragment
import org.tumba.kegel_app.ui.settings.SettingsFragment
import org.tumba.kegel_app.ui.statistic.StatisticFragment
import org.tumba.kegel_app.worker.ReminderWorker
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, DomainModule::class, PresentationModule::class, ViewModelModule::class,
        ConfigModule::class, DatabaseModule::class]
)
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

    fun inject(obj: CustomExerciseSetupFragment)

    fun inject(obj: AdRewardProUpgradeDialogFragment)

    fun inject(obj: StatisticFragment)
}