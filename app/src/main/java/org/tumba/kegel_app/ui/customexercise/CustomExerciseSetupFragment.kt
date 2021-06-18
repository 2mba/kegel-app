package org.tumba.kegel_app.ui.customexercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentCustomExerciseSetupBinding
import org.tumba.kegel_app.databinding.LayoutNumberPickerDialogBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import org.tumba.kegel_app.utils.fragment.setToolbar
import org.tumba.kegel_app.utils.observeEvent
import javax.inject.Inject


class CustomExerciseSetupFragment : Fragment() {

    private lateinit var binding: FragmentCustomExerciseSetupBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CustomExerciseSetupViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomExerciseSetupBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupInsets()
        setupActionBar()
        observeNavigation(viewModel)
        viewModel.observeSnackbar(viewLifecycleOwner, requireContext(), binding.root) {
            anchorView = activity?.findViewById(R.id.navView)
        }
    }

    private fun setupActionBar() {
        setToolbar(binding.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initViews() {
        viewModel.showTenseDurationSelectorDialog.observeEvent(viewLifecycleOwner) { isShowDialog ->
            if (isShowDialog) showTenseDurationSelectorDialog()
        }
        viewModel.showRelaxDurationSelectorDialog.observeEvent(viewLifecycleOwner) { isShowDialog ->
            if (isShowDialog) showRelaxDurationSelectorDialog()
        }
        viewModel.showRepeatsSelectorDialog.observeEvent(viewLifecycleOwner) { isShowDialog ->
            if (isShowDialog) showRepeatsDurationSelectorDialog()
        }
    }

    private fun showTenseDurationSelectorDialog() {
        showNumberPickerDialog(
            title = R.string.screen_custom_exercise_tense_duration_picker_title,
            value = viewModel.tenseDuration.value ?: 0,
            min = 1,
            max = TENSE_RELAX_MAX
        ) { number ->
            viewModel.onTenseDurationSelected(number)
        }
    }

    private fun showRelaxDurationSelectorDialog() {
        showNumberPickerDialog(
            title = R.string.screen_custom_exercise_relax_duration_picker_title,
            value = viewModel.relaxDuration.value ?: 0,
            min = 1,
            max = TENSE_RELAX_MAX
        ) { number ->
            viewModel.onRelaxDurationSelected(number)
        }
    }

    private fun showRepeatsDurationSelectorDialog() {
        showNumberPickerDialog(
            title = R.string.screen_custom_exercise_repeats_picker_title,
            value = viewModel.repeats.value ?: 0,
            min = 1,
            max = REPEATS_MAX
        ) { number ->
            viewModel.onRepeatsSelected(number)
        }
    }

    @Suppress("SameParameterValue")
    private fun showNumberPickerDialog(
        @StringRes title: Int,
        value: Int,
        min: Int,
        max: Int,
        onNumberPickerListener: (Int) -> Unit
    ) {
        val view = LayoutNumberPickerDialogBinding.inflate(layoutInflater)
        view.picker.value = value
        view.picker.minValue = min
        view.picker.maxValue = max
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(view.root)
            .setPositiveButton(android.R.string.ok) { _, _ -> onNumberPickerListener.invoke(view.picker.value) }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun onBackPressed() {
        findNavController().navigateUp()
    }

    private fun setupInsets() {
        binding.toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }
        binding.root.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    companion object {
        private const val REPEATS_MAX = 100
        private const val TENSE_RELAX_MAX = 60 * 3
    }
}
