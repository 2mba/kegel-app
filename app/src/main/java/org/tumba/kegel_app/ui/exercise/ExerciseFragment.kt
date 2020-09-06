package org.tumba.kegel_app.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseBinding
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.Paused
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.Playing
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.InjectorUtils
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.setToolbar
import org.tumba.kegel_app.utils.observe


class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding

    private val viewModel: ExerciseViewModel by viewModels {
        InjectorUtils.provideExerciseViewModelFactory(requireContext())
    }
    private val progressInterpolator = AccelerateDecelerateInterpolator()
    private var lastAnimation: Animation? = null
    private var timerAnimation: Animation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initUi()
        observeViewModel()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    private fun initUi() {
        setupActionBar()
        binding.progress.max = PROGRESS_MAX
        binding.btnPlay.setOnClickListener { viewModel.onClickPlay() }
        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onVibrationStateChanged(isChecked)
        }
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onNotificationStateChanged(isChecked)
        }
    }

    private fun setupActionBar() {
        setToolbar(binding.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = String.Empty
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    private fun observeViewModel() {
        observeExerciseState()

        observe(viewModel.exerciseProgress) { progressValue ->
            lastAnimation?.cancel()
            val progressAnimation = ProgressBarAnimation(
                progressBar = binding.progress,
                from = binding.progress.progress.toFloat(),
                to = progressValue * binding.progress.max
            ).apply {
                interpolator = progressInterpolator
                duration = PROGRESS_ANIMATION_DURATION_MILLIS
            }
            lastAnimation = progressAnimation
            binding.progress.startAnimation(progressAnimation)
        }
    }

    private fun observeExerciseState() {
        observe(viewModel.exercisePlaybackState) { exerciseState ->
            updatePlayButtonState(exerciseState)
            updateTimerAnimation(exerciseState)
        }
    }

    private fun updatePlayButtonState(playbackState: ExercisePlaybackStateUiModel) {
        val btnPlayText: String
        val btnPlayIcon: Int
        when (playbackState) {
            Playing -> {
                btnPlayText = getString(R.string.screen_exercise_btn_pause)
                btnPlayIcon = R.drawable.ic_pause_animated
            }
            Paused -> {
                btnPlayText = getString(R.string.screen_exercise_btn_play)
                btnPlayIcon = R.drawable.ic_play_animated
            }
        }
        val iconDrawable = AnimatedVectorDrawableCompat.create(requireContext(), btnPlayIcon)
        binding.btnPlay.text = btnPlayText
        binding.btnPlay.icon = iconDrawable
        iconDrawable?.start()
    }

    private fun updateTimerAnimation(playbackState: ExercisePlaybackStateUiModel) {
        when (playbackState) {
            Playing -> {
                timerAnimation?.cancel()
            }
            Paused -> {
                timerAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink_animation)
                    ?.also { binding.timer.startAnimation(it) }
            }
        }
    }

    companion object {
        private const val PROGRESS_MAX = 1000
        private const val PROGRESS_ANIMATION_DURATION_MILLIS = 200L
    }

    private class ProgressBarAnimation(
        private val progressBar: ProgressBar,
        private val from: Float,
        private val to: Float
    ) : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
        }
    }
}