package org.tumba.kegel_app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.tumba.kegel_app.databinding.FragmentSettingsBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
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
    }

    private fun initViews() {
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onReminderDayEnabledChanged(isChecked)
        }
        viewModel.showReminderTimePickerDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog.getContentIfNotHandled() == true) {
                showReminderTimePicker()
            }
        }
    }

    private fun showReminderTimePicker() {
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select reminder time")
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.onReminderTimeSelected(hour, minute)
                }
                show(this@SettingsFragment.childFragmentManager, MaterialTimePicker::class.java.name)
            }
    }
}
