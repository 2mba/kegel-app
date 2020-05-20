package org.tumba.kegel_app.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.home_item_exercise.*
import kotlinx.android.synthetic.main.home_item_hint.*
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentHomeBinding
import org.tumba.kegel_app.utils.InjectorUtils
import org.tumba.kegel_app.utils.observe


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireContext())
    }

    private val rootNavController: NavController? by lazy {
        (parentFragment as NavHost?)?.navController
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController()
        observeViewModel()
        btnStartExercise.setOnClickListener { navigateToExercise() }
        itemHint.setOnClickListener { }
    }

    private fun navigateToExercise() {
        rootNavController?.navigate(
            R.id.action_screen_home_to_screen_exercise,
            null,
            null,
            FragmentNavigatorExtras(
                itemExercise to "itemExercise"
            )
        )
    }

    private fun observeViewModel() {
        observe(viewModel.exerciseDay) { exerciseDay ->
            day.text = getString(R.string.screen_home_day_pattern, exerciseDay.toString())
        }
        observe(viewModel.exerciseLevel) { exerciseLevel ->
            level.text = getString(R.string.screen_home_level_pattern, exerciseLevel.toString())
        }
    }
}
