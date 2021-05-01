package org.tumba.kegel_app.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.BuildConfig
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentSettingsBinding
import org.tumba.kegel_app.databinding.LayoutLevelPickerDialogBinding
import org.tumba.kegel_app.databinding.LayoutSoundVolumePickerDialogBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import org.tumba.kegel_app.utils.observeEvent
import javax.inject.Inject


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupInsets()
        viewModel.observeSnackbar(viewLifecycleOwner, requireContext(), binding.root) {
            anchorView = activity?.findViewById(R.id.navView)
        }
    }

    private fun initViews() {
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onReminderDayEnabledChanged(isChecked)
        }
        viewModel.showReminderTimePickerDialog.observeEvent(viewLifecycleOwner) {
            showReminderTimePicker()
        }
        viewModel.showLevelPickerDialog.observeEvent(viewLifecycleOwner) {
            showLevelPicker()
        }
        viewModel.startReview.observeEvent(viewLifecycleOwner) {
            openGooglePlayAppPage()
        }
        viewModel.showNightModeDialog.observeEvent(viewLifecycleOwner) {
            showNightModePickerDialog()
        }
        viewModel.showSoundLevelPickerDialog.observeEvent(viewLifecycleOwner) {
            showSoundVolumePickerDialog()
        }
    }

    private fun showReminderTimePicker() {
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(viewModel.reminderTime.value?.hour ?: 0)
            .setMinute(viewModel.reminderTime.value?.minute ?: 0)
            .setTitleText(R.string.screen_settings_reminder_time_dialog_title)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.onReminderTimeSelected(hour, minute)
                }
                show(this@SettingsFragment.childFragmentManager, MaterialTimePicker::class.java.name)
            }
    }

    private fun showLevelPicker() {
        val view = LayoutLevelPickerDialogBinding.inflate(layoutInflater)
        view.picker.value = viewModel.level.value
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.screen_settings_level_dialog_title)
            .setView(view.root)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onLevelSelected(view.picker.value) }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun openGooglePlayAppPage() {
        val packageName = BuildConfig.APPLICATION_ID
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun showNightModePickerDialog() {
        val nightModeValues = arrayOf(
            getString(R.string.screen_settings_night_mode_system),
            getString(R.string.screen_settings_night_mode_on),
            getString(R.string.screen_settings_night_mode_off)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.screen_settings_night_mode_title)
            .setItems(nightModeValues) { _, selected ->
                val value = when (selected) {
                    0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    2 -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
                viewModel.onNightModeSelected(value)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun showSoundVolumePickerDialog() {
        val binding = LayoutSoundVolumePickerDialogBinding.inflate(layoutInflater)
        binding.slider.value = (viewModel.soundVolume.value ?: 0).toFloat()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.screen_settings_sound_volume_title)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok)  { _, _ ->
                viewModel.onSoundVolumeSelected(binding.slider.value.toInt())
            }
            .create()
            .show()
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

}
