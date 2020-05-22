package org.tumba.kegel_app.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseBinding
import org.tumba.kegel_app.ui.exercise.ExerciseStateUiModel.Paused
import org.tumba.kegel_app.ui.exercise.ExerciseStateUiModel.Playing
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

            progress.max = PROGRESS_MAX
        }

        binding.btnPlay.setOnClickListener { viewModel.onClickPlay() }
        binding.btnNotification.setOnClickListener { viewModel.onClickNotification() }
        binding.btnVibration.setOnClickListener { viewModel.onClickVibration() }
    }

    private fun observeViewModel() {
        observeExerciseState()

        observe(viewModel.exerciseProgress) { progressValue ->
            val interpolatedProgress = progressInterpolator.getInterpolation(progressValue)
            binding.progress.progress = (interpolatedProgress * binding.progress.max).toInt()
        }
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
}