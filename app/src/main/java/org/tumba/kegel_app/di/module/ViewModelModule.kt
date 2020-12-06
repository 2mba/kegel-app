package org.tumba.kegel_app.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.tumba.kegel_app.ui.exercise.ExerciseResultViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseViewModel
import org.tumba.kegel_app.ui.home.HomeViewModel
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
}


@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
