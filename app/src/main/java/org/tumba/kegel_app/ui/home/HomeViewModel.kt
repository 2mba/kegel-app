package org.tumba.kegel_app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.tumba.kegel_app.analytics.HomeScreenTracker
import org.tumba.kegel_app.config.RemoteConfigFetcher
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.interactor.CustomExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseType
import org.tumba.kegel_app.utils.Event
import org.tumba.kegel_app.utils.formatExerciseDuration
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class HomeViewModel @Inject constructor(
    exerciseParametersProvider: ExerciseParametersProvider,
    remoteConfigFetcher: RemoteConfigFetcher,
    customExerciseInteractor: CustomExerciseInteractor,
    private val progressViewedStore: ProgressViewedStore,
    private val exerciseInteractor: ExerciseInteractor,
    private val tracker: HomeScreenTracker
) : BaseViewModel() {

    val exerciseLevel = exerciseParametersProvider.observeLevel().asLiveData()
    val exerciseDay = exerciseParametersProvider.observeDay().asLiveData()
    val numberOfExercises = exerciseParametersProvider.observeNumberOfCompletedExercises().asLiveData()
    val allExercisesDuration = exerciseParametersProvider.observeAllExercisesDurationInSeconds()
        .map { it.seconds.toInt(DurationUnit.MINUTES) }
        .asLiveData()

    val nextExercisesDuration = exerciseParametersProvider.observeNextExerciseDurationInSeconds()
        .map(::formatExerciseDuration)
        .asLiveData()

    val progress = exerciseParametersProvider.observeProgress().asLiveData()
    val progressAnimation = MutableLiveData<Event<Boolean>>()

    val isStartCustomExerciseVisible = customExerciseInteractor.observeIsCustomExerciseConfigured().asLiveData()

    init {
        remoteConfigFetcher.fetchAndActivate()
    }

    fun onResume() {
        if (!exerciseInteractor.isUserAgreementConfirmed()) {
            navigate(HomeFragmentDirections.actionScreenHomeToFirstEntryDialogFragment())
        }
        if (!progressViewedStore.isProgressViewed) {
            progressViewedStore.isProgressViewed = true
            progressAnimation.value = Event(true)
        }
    }

    fun onStartExerciseClicked() {
        if (exerciseInteractor.isThereCompletedExercise() || exerciseInteractor.isFirstExerciseChallengeShown()) {
            navigate(HomeFragmentDirections.actionScreenHomeToScreenExercise(ExerciseType.Predefined))
        } else {
            viewModelScope.launch {
                exerciseInteractor.setFirstExerciseChallengeShown()
                navigate(
                    HomeFragmentDirections.actionScreenHomeToFirstExerciseChallengeDialogFragment(ExerciseType.Predefined)
                )
            }
        }
    }

    fun onShowHintClicked() {
        navigate(
            HomeFragmentDirections.actionScreenHomeToExerciseInfoFragment(
                showExerciseButton = false,
                type = ExerciseType.Predefined
            )
        )
    }

    fun onStartCustomExerciseClicked() {
        tracker.trackStartCustomExercise()
        navigate(HomeFragmentDirections.actionScreenHomeToScreenExercise(ExerciseType.Custom))
    }

    fun onConfigureCustomExerciseClicked() {
        if (isStartCustomExerciseVisible.value == false) {
            tracker.trackFirstTimeCustomizeClicked()
        } else {
            tracker.trackCustomizeClicked()
        }
        navigate(HomeFragmentDirections.actionScreenHomeToCustomExerciseSetupFragment())
    }
}

