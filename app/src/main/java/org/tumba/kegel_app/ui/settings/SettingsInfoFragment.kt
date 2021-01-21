package org.tumba.kegel_app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.tumba.kegel_app.databinding.FragmentExerciseInfoBinding
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.fragment.actionBar
import org.tumba.kegel_app.utils.fragment.setToolbar


class SettingsInfoFragment : Fragment() {

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

    private fun setupActionBar() {
        setToolbar(binding.toolbar)
        actionBar?.title = String.Empty
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun onBackPressed() {
        findNavController().navigateUp()
    }
}
