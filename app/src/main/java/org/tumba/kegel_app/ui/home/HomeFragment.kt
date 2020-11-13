package org.tumba.kegel_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import org.tumba.kegel_app.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.exerciseItem.btnStartExercise.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionScreenHomeToScreenExercise(),
                FragmentNavigatorExtras(
                    binding.exerciseItem.itemExercise to "itemExercise"
                )
            )
        }
        binding.hintItem.btnHint.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionScreenHomeToExerciseInfoFragment(),
                FragmentNavigatorExtras(
                    binding.hintItem.itemHint to "itemHint"
                )
            )
        }
        return binding.root
    }
}
