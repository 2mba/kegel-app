package org.tumba.kegel_app.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.tumba.kegel_app.ui.customexercise.CustomExerciseSetupViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseResultViewModel
import org.tumba.kegel_app.ui.home.HomeViewModel
import org.tumba.kegel_app.ui.home.dialog.AdRewardProUpgradeViewModel
import org.tumba.kegel_app.ui.home.dialog.FirstEntryViewModel
import org.tumba.kegel_app.ui.home.dialog.FirstExerciseChallengeViewModel
import org.tumba.kegel_app.ui.home.dialog.FreePeriodSuggestionViewModel
import org.tumba.kegel_app.ui.proupgrade.ProUpgradeViewModel
import org.tumba.kegel_app.ui.settings.AboutAppViewModel
import org.tumba.kegel_app.ui.settings.SettingsViewModel
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

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

    @Binds
    @IntoMap
    @ViewModelKey(CustomExerciseSetupViewModel::class)
    internal abstract fun customExerciseSetupViewModel(viewModel: CustomExerciseSetupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdRewardProUpgradeViewModel::class)
    internal abstract fun adRewardProUpgradeViewModel(viewModel: AdRewardProUpgradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FreePeriodSuggestionViewModel::class)
    internal abstract fun freePeriodSuggestionViewModel(viewModel: FreePeriodSuggestionViewModel): ViewModel
}


@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
