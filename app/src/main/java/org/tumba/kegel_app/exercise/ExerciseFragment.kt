package org.tumba.kegel_app.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseBinding
import org.tumba.kegel_app.exercise.ExerciseStateUiModel.Paused
import org.tumba.kegel_app.exercise.ExerciseStateUiModel.Playing
import org.tumba.kegel_app.utils.InjectorUtils
import org.tumba.kegel_app.utils.observe


class ExerciseFragment : Fragment() {
    companion object {
        private const val DELAY_BUTTONS_APPEARING_MILLIS = 400L
        private const val PROGRESS_MAX = 1000
    }

    private lateinit var binding: FragmentExerciseBinding

    private val viewModel: ExerciseViewModel by viewModels {
        InjectorUtils.provideExerciseViewModelFactory(requireContext())
    }
    private val progressInterpolator = AccelerateDecelerateInterpolator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeViewModel()
    }

    private fun initUi() {
        binding.apply {
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
    }

    private fun observeViewModel() {
        observeExerciseKind()
        observeExerciseState()
        observeVibrationState()

        observe(viewModel.repeatsRemain) { repeatsRemain ->
            binding.repeats.text = repeatsRemain.toString()
        }
        observe(viewModel.secondsRemain) { secondsRemain -> binding.timer.text = "00:0$secondsRemain" }
        observe(viewModel.exerciseProgress) { progressValue ->
            val interpolatedProgress = progressInterpolator.getInterpolation(progressValue)
            binding.progress.progress = (interpolatedProgress * binding.progress.max).toInt()
        }
        observe(viewModel.day) { dayValue -> binding.day.text = dayValue.toString() }
        observe(viewModel.level) { levelValue -> binding.level.text = levelValue.toString() }
    }

    private fun observeExerciseKind() {
        viewModel.exerciseKind.observe(
            viewLifecycleOwner,
            Observer { exerciseValue -> binding.exercise.text = exerciseValue }
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
            binding.btnPlay.text = btnPlayText
            binding.btnPlay.icon = iconDrawable
            iconDrawable?.start()
        }
    }

    private fun observeVibrationState() {
        viewModel.isVibrationEnabled.observe(
            viewLifecycleOwner,
            Observer { isVibrationEnabled ->
                binding.btnVibration.icon = if (isVibrationEnabled) {
                    VectorDrawableCompat.create(resources, R.drawable.ic_notification_off, null)
                } else {
                    VectorDrawableCompat.create(resources, R.drawable.ic_vibration, null)
                }
            }
        )
    }
}