package org.tumba.kegel_app.ui.exercise

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import nl.dionsegijn.konfetti.models.Size
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseResultBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import javax.inject.Inject


class ExerciseResultFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ExerciseResultViewModel by viewModels { viewModelFactory }
    private lateinit var binding: FragmentExerciseResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentExerciseResultBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        initUi()
        isCancelable = false
        return MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setView(binding.root)
            .create()
            .also { it.setOnShowListener { showConfetti() } }
    }

    private fun initUi() {
        binding.btnDone.setOnClickListener { navigateToHomeScreen() }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToHomeScreen()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.onDismiss()
    }

    private fun showConfetti() {
        val colors = listOf(
            R.color.confetti_1,
            R.color.confetti_2,
            R.color.confetti_3,
            R.color.confetti_4
        ).map { ResourcesCompat.getColor(resources, it, null) }
        binding.konfetti.build()
            .addColors(colors)
            .setDirection(0.0, 359.0)
            .setSpeed(0.5f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addSizes(Size(8), Size(4))
            .setPosition(-50f, binding.konfetti.measuredWidth + 50f, -50f, -50f)
            .streamFor(200, 700L)
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(ExerciseResultFragmentDirections.actionExerciseResultFragmentToScreenHome())
    }

}