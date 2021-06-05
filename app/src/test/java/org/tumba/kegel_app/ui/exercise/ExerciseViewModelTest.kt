package org.tumba.kegel_app.ui.exercise

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be`
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.tumba.kegel_app.analytics.ExerciseTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.core.system.PermissionProvider
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseServiceInteractor
import org.tumba.kegel_app.factory.ExerciseStateFactory
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.ad.ExerciseBannerAdShowBehaviour
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import org.tumba.kegel_app.util.MainDispatcherInitializerRule

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseViewModelTest {

    @Rule
    @JvmField
    val mainDispatcherInitializerRule = MainDispatcherInitializerRule()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    lateinit var exerciseInteractor: ExerciseInteractor

    @RelaxedMockK
    lateinit var exerciseServiceInteractor: ExerciseServiceInteractor

    @RelaxedMockK
    lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    @RelaxedMockK
    lateinit var exerciseParametersProvider: ExerciseParametersProvider

    @RelaxedMockK
    lateinit var exerciseNameProvider: ExerciseNameProvider

    @RelaxedMockK
    lateinit var resourceProvider: ResourceProvider

    @RelaxedMockK
    lateinit var tracker: ExerciseTracker

    @RelaxedMockK
    lateinit var proUpgradeManager: ProUpgradeManager

    @RelaxedMockK
    lateinit var permissionProvider: PermissionProvider

    @RelaxedMockK
    lateinit var exerciseBannerAdShowBehaviour: ExerciseBannerAdShowBehaviour

    private lateinit var viewModel: ExerciseViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Ignore
    @Test
    fun `should create and start exercise when screen opened and no in progress exercise`() {
        // Arrange
        coEvery { exerciseInteractor.isExerciseInProgress() } returns false
        // Act
        createViewModel()
        // Assert
        coVerifySequence {
            exerciseInteractor.clearExercise()
            exerciseInteractor.createExercise()
            exerciseInteractor.startExercise()
        }
    }

    @Ignore
    @Test
    fun `should show correct single exercise remain time`(): Unit = runBlockingTest {
        // Arrange
        coEvery { exerciseInteractor.isExerciseInProgress() } returns true
        coEvery { exerciseInteractor.observeExerciseState() } returns flow {
            ExerciseState.Holding(
                singleExerciseInfo = ExerciseStateFactory.singleExerciseInfo(remainSeconds = 65),
                exerciseInfo = ExerciseStateFactory.exerciseInfo()
            )
            ExerciseState.Holding(
                singleExerciseInfo = ExerciseStateFactory.singleExerciseInfo(remainSeconds = 65),
                exerciseInfo = ExerciseStateFactory.exerciseInfo()
            )
        }
        // Act
        createViewModel()
        val timeRemainObserver = Observer<Any> { println("$it") }
        viewModel.timeRemain.observeForever(timeRemainObserver)
        //mainDispatcherInitializerRule.dispatcher.advanceTimeBy(3000)
        delay(3000)
        // Assert
        viewModel.timeRemain.value `should be` "01:05"
    }

    private fun createViewModel() {
        viewModel = ExerciseViewModel(
            exerciseInteractor,
            exerciseServiceInteractor,
            exerciseSettingsRepository,
            exerciseParametersProvider,
            exerciseNameProvider,
            proUpgradeManager,
            tracker,
            resourceProvider,
            permissionProvider,
            exerciseBannerAdShowBehaviour
        )
    }
}