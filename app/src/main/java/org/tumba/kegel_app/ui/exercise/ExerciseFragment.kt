package org.tumba.kegel_app.ui.exercise

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.R
import org.tumba.kegel_app.config.AppBuildConfig
import org.tumba.kegel_app.databinding.FragmentExerciseBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.exercise.ExerciseFragmentDirections.Companion.actionScreenExerciseToExerciseInfoFragment
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.*
import org.tumba.kegel_app.ui.utils.MenuVisibilityObserver
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import org.tumba.kegel_app.utils.fragment.setToolbar
import org.tumba.kegel_app.utils.observe
import org.tumba.kegel_app.utils.observeEvent
import javax.inject.Inject


class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var appBuildConfig: AppBuildConfig

    private val viewModel: ExerciseViewModel by viewModels { viewModelFactory }
    private var lastAnimation: Animation? = null
    private var timerAnimation: Animation? = null

    private var dialogs = mutableListOf<DialogInterface>()

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
        observeNavigation(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        viewModel.observeSnackbar(viewLifecycleOwner, requireContext(), view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.exercise_menu, menu)
        viewModel.isProAvailable
            .map { !it }
            .observe(viewLifecycleOwner, MenuVisibilityObserver(binding.toolbar, R.id.upgradeToPro))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                viewModel.onHelpClicked()
                findNavController().navigate(actionScreenExerciseToExerciseInfoFragment(showExerciseButton = false))
                true
            }
            R.id.upgradeToPro -> {
                viewModel.onMenuUpgradeToProClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DRAW_OVER_APP_REQUEST_CODE) {
            viewModel.onDrawOverAppResult()
        }
    }

    private fun initUi() {
        setupActionBar()
        binding.progress.max = PROGRESS_MAX
        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onVibrationStateChanged(isChecked)
        }
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.isProAvailable.value == true) {
                viewModel.onSoundStateChanged(isChecked)
            } else {
                binding.soundSwitch.isChecked = viewModel.isSoundEnabled.value ?: false
                if (binding.soundSwitch.isChecked != isChecked) {
                    viewModel.onClickSound()
                }
            }
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
        observeBannerAds()
        viewLifecycleOwner.observe(viewModel.exerciseProgress) { progressValue ->
            lastAnimation?.cancel()
            val progressAnimation = ProgressBarAnimation(
                progressBar = binding.progress,
                from = binding.progress.progress.toFloat(),
                to = progressValue * binding.progress.max
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                duration = PROGRESS_ANIMATION_DURATION_MILLIS
            }
            lastAnimation = progressAnimation
            binding.progress.startAnimation(progressAnimation)
        }
        viewLifecycleOwner.observe(viewModel.exerciseProgressColor) { colorRes ->
            val color = ResourcesCompat.getColor(resources, colorRes, null)
            binding.progress.progressTintList = ColorStateList.valueOf(color)
            binding.exercise.backgroundTintList = ColorStateList.valueOf(color)
        }
        viewLifecycleOwner.observeEvent(viewModel.exitConfirmationDialogVisible) { visible ->
            if (visible) showConfirmationDialog()
        }
        viewLifecycleOwner.observeEvent(viewModel.exit) { exit ->
            if (exit) findNavController().popBackStack()
        }
        viewLifecycleOwner.observeEvent(viewModel.navigateToExerciseResult) { navigate ->
            if (navigate) navigateToExerciseResultFragment()
        }
        viewLifecycleOwner.observeEvent(viewModel.showBackgroundModeDialog) { showBackgroundModeDialog ->
            if (showBackgroundModeDialog) showBackgroundModeDialog()
        }
        viewLifecycleOwner.observeEvent(viewModel.showDrawOverAppsDialog) { showDrawOverAppsDialog ->
            if (showDrawOverAppsDialog) showDrawOverAppsDialog()
        }
        viewLifecycleOwner.observeEvent(viewModel.navigateToDrawOverAppSettings) { navigateToDrawOverAppSettings ->
            if (navigateToDrawOverAppSettings) navigateToDrawOverAppSettings()
        }
        viewLifecycleOwner.observeEvent(viewModel.dismissShownDialogs) { dismissShownDialogs ->
            if (dismissShownDialogs) dismissShownDialogs()
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

    private fun setupInsets() {
        view?.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
        binding.toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }
    }

    private fun showBackgroundModeDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.screen_exercise_background_mode_title)
            .setAdapter(
                BackgroundModeSelectorAdapter(
                    context = requireContext(),
                    isProAvailable = viewModel.isProAvailable.value ?: false
                )
            ) { _, selected ->
                viewModel.onBackgroundModeSelected(ExerciseBackgroundMode.values()[selected])
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
        dialogs.add(dialog)
        dialog.show()
    }

    private fun showDrawOverAppsDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.screen_exercise_draw_over_app_dialog_title)
            .setView(R.layout.layout_draw_over_apps_dialog)
            .setPositiveButton(R.string.screen_exercise_draw_over_app_dialog_btn_ok) { _, _ ->
                viewModel.onConfirmedDrawOverApp()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
        dialogs.add(dialog)
        dialog.show()
    }

    private fun navigateToDrawOverAppSettings() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireContext().packageName)
            )
            startActivityForResult(intent, DRAW_OVER_APP_REQUEST_CODE)
        }
    }

    private fun dismissShownDialogs() {
        dialogs.forEach { it.dismiss() }
        dialogs.clear()
    }

    private fun observeBannerAds() {
        val adView = AdView(requireContext())
        adView.adUnitId = appBuildConfig.exerciseBannerAppAdsUnitId
        adView.adSize = AdSize.BANNER
        binding.adViewContainer.addView(adView)
        if (viewModel.isBannerAdsShown) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    companion object {
        private const val PROGRESS_MAX = 1000
        private const val DRAW_OVER_APP_REQUEST_CODE = 100
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
