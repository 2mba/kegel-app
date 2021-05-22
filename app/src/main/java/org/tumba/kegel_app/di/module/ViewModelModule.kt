package org.tumba.kegel_app.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.tumba.kegel_app.ui.exercise.ExerciseResultViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseViewModel
import org.tumba.kegel_app.ui.home.FirstExerciseChallengeViewModel
import org.tumba.kegel_app.ui.home.HomeViewModel
import org.tumba.kegel_app.ui.proupgrade.ProUpgradeViewModel
import org.tumba.kegel_app.ui.settings.AboutAppViewModel
import org.tumba.kegel_app.ui.settings.SettingsViewModel
import org.tumba.vacuum_app.ui.home.FirstEntryViewModel
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseViewModel::class)
    internal abstract fun exerciseViewModel(viewModel: ExerciseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseResultViewModel::class)
    internal abstract fun exerciseResultViewModel(viewModel: ExerciseResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FirstExerciseChallengeViewModel::class)
    internal abstract fun firstExerciseChallengeViewModel(viewModel: FirstExerciseChallengeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProUpgradeViewModel::class)
    internal abstract fun proUpgrageViewModel(viewModel: ProUpgradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FirstEntryViewModel::class)
    internal abstract fun firstEntryViewModel(viewModel: FirstEntryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutAppViewModel::class)
    internal abstract fun aboutAppViewModel(viewModel: AboutAppViewModel): ViewModel
}


@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
