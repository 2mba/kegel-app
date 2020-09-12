package org.tumba.kegel_app.ui.exercise

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import nl.dionsegijn.konfetti.models.Size
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentExerciseResultBinding


class ExerciseResultFragment : DialogFragment() {

    private lateinit var binding: FragmentExerciseResultBinding

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