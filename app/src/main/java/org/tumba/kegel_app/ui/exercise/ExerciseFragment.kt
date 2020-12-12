package org.tumba.kegel_app.ui.exercise

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.*
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.setToolbar
import org.tumba.kegel_app.utils.observe
import javax.inject.Inject


class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ExerciseViewModel by viewModels { viewModelFactory }
    private val progressInterpolator = AccelerateDecelerateInterpolator()
    private var lastAnimation: Animation? = null
    private var timerAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setHasOptionsMenu(true)
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.exercise_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                viewModel.onHelpClicked()
                findNavController().navigate(ExerciseFragmentDirections.actionScreenExerciseToExerciseInfoFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUi() {
        setupActionBar()
        binding.progress.max = PROGRESS_MAX
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
        binding.toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.onBackPressed()
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.screen_exercise_exit_confirmation_dialog_message)
            .setTitle(R.string.screen_exercise_exit_confirmation_dialog_title)
            .setPositiveButton(R.string.screen_exercise_exit_confirmation_dialog_exit) { _, _ ->
                viewModel.onExitConfirmed()
            }
            .setNegativeButton(R.string.screen_exercise_exit_confirmation_dialog_stay) { dialog, _ ->
                dialog.cancel()
            }
            .setOnCancelListener { viewModel.onConfirmationDialogCanceled() }
            .create()
            .show()
    }

    private fun observeViewModel() {
        observeExerciseState()

        viewLifecycleOwner.observe(viewModel.exerciseProgress) { progressValue ->
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
        viewLifecycleOwner.observe(viewModel.exitConfirmationDialogVisible) { visible ->
            if (visible.getContentIfNotHandled() == true) {
                showConfirmationDialog()
            }
        }
        viewLifecycleOwner.observe(viewModel.exit) { exit ->
            if (exit.getContentIfNotHandled() == true) {
                findNavController().popBackStack()
            }
        }
        viewLifecycleOwner.observe(viewModel.navigateToExerciseResult) { navigate ->
            if (navigate.getContentIfNotHandled() == true) {
                navigateToExerciseResultFragment()
            }
        }
    }

    private fun observeExerciseState() {
        observe(viewModel.exercisePlaybackState) { exerciseState ->
            updatePlayButtonState(exerciseState)
            updateTimerAnimation(exerciseState)
        }
    }

    private fun updatePlayButtonState(playbackState: ExercisePlaybackStateUiModel) {
        val btnPlayIcon = when (playbackState) {
            Playing -> R.drawable.ic_pause_animated
            Paused,
            Stopped -> R.drawable.ic_play_animated
        }
        val iconDrawable = AnimatedVectorDrawableCompat.create(requireContext(), btnPlayIcon)
        binding.btnPlay.icon = iconDrawable
        iconDrawable?.start()
    }

    private fun updateTimerAnimation(playbackState: ExercisePlaybackStateUiModel) {
        when (playbackState) {
            Playing,
            Stopped -> {
                timerAnimation?.cancel()
            }
            Paused -> {
                timerAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink_animation)
                    ?.also { binding.timer.startAnimation(it) }
            }
        }
    }

    private fun navigateToExerciseResultFragment() {
        findNavController().navigate(ExerciseFragmentDirections.actionScreenExerciseToExerciseResultFragment())
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