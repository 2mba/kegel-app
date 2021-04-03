package org.tumba.kegel_app.ui.home

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFade
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.databinding.FragmentExerciseInfoBinding
import org.tumba.kegel_app.ui.home.ExerciseInfoFragmentDirections.Companion.actionScreenExerciseInfoFragmentToScreenExercise
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.setToolbar
import org.tumba.kegel_app.utils.show


class ExerciseInfoFragment : Fragment() {

    private lateinit var binding: FragmentExerciseInfoBinding
    private val args by navArgs<ExerciseInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseInfoBinding.inflate(inflater, container, false)
        setupActionBar()
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
    }

    private fun setupActionBar() {
        setToolbar(binding.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initViews() {
        binding.btnDone.setOnClickListener {
            findNavController().navigate(actionScreenExerciseInfoFragmentToScreenExercise())
        }
        if (args.showExerciseButton) {
            binding.btnDone.postDelayed(BUTTON_ANIMATION_DELAY_MILLIS) {
                val transition = MaterialFade()
                    .setInterpolator(AnticipateOvershootInterpolator(BUTTON_ANIMATION_INTERPOLATOR_TENSION))
                    .setDuration(BUTTON_ANIMATION_INTERPOLATOR_DURATION_MILLIS)
                TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
                binding.btnDone.show()
            }
        }
        binding.otherText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun onBackPressed() {
        findNavController().navigateUp()
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

    companion object {
        private const val BUTTON_ANIMATION_DELAY_MILLIS = 250L
        private const val BUTTON_ANIMATION_INTERPOLATOR_TENSION = 15f
        private const val BUTTON_ANIMATION_INTERPOLATOR_DURATION_MILLIS = 500L
    }
}
