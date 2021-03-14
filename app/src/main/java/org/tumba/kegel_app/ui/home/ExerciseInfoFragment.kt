package org.tumba.kegel_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.databinding.FragmentExerciseInfoBinding
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.setToolbar


class ExerciseInfoFragment : Fragment() {

    private lateinit var binding: FragmentExerciseInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseInfoBinding.inflate(inflater, container, false)
        setupActionBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
    }

    private fun setupActionBar() {
        setToolbar(binding.toolbar)
        actionBar?.title = String.Empty
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
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

}
