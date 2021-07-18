package org.tumba.kegel_app.ui.statistic

import org.tumba.kegel_app.analytics.HomeScreenTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.config.RemoteConfigFetcher
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.interactor.CustomExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class StatisticViewModel @Inject constructor(
    exerciseParametersProvider: ExerciseParametersProvider,
    remoteConfigFetcher: RemoteConfigFetcher,
    customExerciseInteractor: CustomExerciseInteractor,
    private val exerciseInteractor: ExerciseInteractor,
    private val proUpgradeManager: ProUpgradeManager,
    private val tracker: HomeScreenTracker
) : BaseViewModel() {

}

