package org.tumba.kegel_app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.tumba.kegel_app.analytics.HomeScreenTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
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
    private val proUpgradeManager: ProUpgradeManager,
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
    val isShowExpirationDialog = MutableLiveData<Event<Boolean>>()

    private var isDefaultFreePeriodDialogShown = true

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
        checkDefaultFreePeriodExpiration()
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

    fun onUpgradeToProClicked() {
        if (isDefaultFreePeriodDialogShown) {
            tracker.trackDefaultFreePeriodExpiredOkClicked()
        } else {
            tracker.trackAdRewardFreePeriodExpiredOkClicked()
        }
        tracker.trackNavigateToProUpgradeFromExpiredDialog()
        navigate(HomeFragmentDirections.actionGlobalScreenProUpgrade())
    }

    fun onUpgradeToProCanceled() {
        if (isDefaultFreePeriodDialogShown) {
            tracker.trackDefaultFreePeriodExpiredCancelled()
        } else {
            tracker.trackAdRewardFreePeriodExpiredCancelled()
        }
    }

    private fun checkDefaultFreePeriodExpiration() {
        viewModelScope.launch {
            delay(1.seconds)
            val defaultFreePeriodState = proUpgradeManager.defaultFreePeriodState.value
            if (defaultFreePeriodState is ProUpgradeManager.FreePeriodState.Expired &&
                !defaultFreePeriodState.isExpirationShown &&
                !proUpgradeManager.isProAvailable.value
            ) {
                viewModelScope.launch {
                    tracker.trackDefaultFreePeriodExpiredShown()
                    proUpgradeManager.setDefaultFreePeriodExpirationShown()
                    isDefaultFreePeriodDialogShown = true
                    isShowExpirationDialog.value = Event(true)
                }
            } else {
                checkAdRewardFreePeriodExpiration()
            }
        }
    }

    private fun checkAdRewardFreePeriodExpiration() {
        viewModelScope.launch {
            val adAwardFreePeriodState = proUpgradeManager.adAwardFreePeriodState.value
            if (adAwardFreePeriodState is ProUpgradeManager.FreePeriodState.Expired &&
                !adAwardFreePeriodState.isExpirationShown &&
                !proUpgradeManager.isProAvailable.value
            ) {
                viewModelScope.launch {
                    tracker.trackAdRewardFreePeriodExpiredShown()
                    proUpgradeManager.setAdRewardFreePeriodExpirationShown()
                    isDefaultFreePeriodDialogShown = false
                    isShowExpirationDialog.value = Event(true)
                }
            }
        }
    }
}

