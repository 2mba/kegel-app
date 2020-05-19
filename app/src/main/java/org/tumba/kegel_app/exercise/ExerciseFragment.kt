package org.tumba.kegel_app.exercise

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlinx.android.synthetic.main.fragment_exercise.*
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.BaseFragment
import org.tumba.kegel_app.core.ResourceCreationStrategy
import org.tumba.kegel_app.core.getViewModel
import org.tumba.kegel_app.di.Scope.SCOPE_APP
import org.tumba.kegel_app.di.Scope.SCOPE_EXERCISE
import org.tumba.kegel_app.exercise.ExerciseStateUiModel.Paused
import org.tumba.kegel_app.exercise.ExerciseStateUiModel.Playing
import org.tumba.kegel_app.utils.observe
import toothpick.Toothpick


class ExerciseFragment : BaseFragment(
    viewCreationStrategy = ResourceCreationStrategy(R.layout.fragment_exercise)
) {
    companion object {
        private const val DELAY_BUTTONS_APPEARING_MILLIS = 400L
        private const val PROGRESS_MAX = 1000
    }

    private lateinit var viewModel: ExerciseViewModel
    private val progressInterpolator = AccelerateDecelerateInterpolator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initScope()
        viewModel = getViewModel(ExerciseViewModel::class, SCOPE_EXERCISE)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeViewModel()
    }

    private fun initScope() {
        Toothpick.openScopes(SCOPE_APP, SCOPE_EXERCISE)
            .installModules(getExerciseModule())
    }

    private fun initUi() {
        btnPlay.visibility = View.INVISIBLE
        btnNotification.visibility = View.INVISIBLE
        btnVibration.visibility = View.INVISIBLE

        btnPlay.postDelayed({ btnPlay.show() },
            DELAY_BUTTONS_APPEARING_MILLIS
        )
        btnNotification.postDelayed({ btnNotification.show() },
            DELAY_BUTTONS_APPEARING_MILLIS
        )
        btnVibration.postDelayed({ btnVibration.show() },
            DELAY_BUTTONS_APPEARING_MILLIS
        )

        btnPlay.setOnClickListener { viewModel.onClickPlay() }
        btnNotification.setOnClickListener { viewModel.onClickNotification() }
        btnVibration.setOnClickListener { viewModel.onClickVibration() }
        progress.max =
            PROGRESS_MAX
    }

    private fun observeViewModel() {
        observeExerciseKind()
        observeExerciseState()
        observeVibrationState()

        observe(viewModel.repeatsRemain) { repeatsRemain ->
            repeats.text = repeatsRemain.toString()
        }
        observe(viewModel.secondsRemain) { secondsRemain -> timer.text = "00:0$secondsRemain" }
        observe(viewModel.exerciseProgress) { progressValue ->
            val interpolatedProgress = progressInterpolator.getInterpolation(progressValue)
            progress.progress = (interpolatedProgress * progress.max).toInt()
        }
        observe(viewModel.day) { dayValue -> day.text = dayValue.toString() }
        observe(viewModel.level) { levelValue -> level.text = levelValue.toString() }
    }

    private fun observeExerciseKind() {
        viewModel.exerciseKind.observe(
            viewLifecycleOwner,
            Observer { exerciseValue -> exercise.text = exerciseValue }
        )
    }

    private fun observeExerciseState() {
        observe(viewModel.exerciseState) { exerciseState ->
            val btnPlayText: String
            val btnPlayIcon: Int
            when (exerciseState) {
                Playing -> {
                    btnPlayText = getString(R.string.screen_exercise_btn_pause)
                    btnPlayIcon = R.drawable.ic_pause_animated
                }
                Paused -> {
                    btnPlayText = getString(R.string.screen_exercise_btn_play)
                    btnPlayIcon = R.drawable.ic_play_animated
                }
                else -> throw IllegalStateException("exerciseState should be not null")
            }
            val iconDrawable = AnimatedVectorDrawableCompat.create(
                requireContext(),
                btnPlayIcon
            )
            btnPlay.text = btnPlayText
            btnPlay.icon = iconDrawable
            iconDrawable?.start()
        }
    }

    private fun observeVibrationState() {
        viewModel.isVibrationEnabled.observe(
            viewLifecycleOwner,
            Observer { isVibrationEnabled ->
                btnVibration.icon = if (isVibrationEnabled) {
                    VectorDrawableCompat.create(resources, R.drawable.ic_notification_off, null)
                } else {
                    VectorDrawableCompat.create(resources, R.drawable.ic_vibration, null)
                }
            }
        )
    }
}