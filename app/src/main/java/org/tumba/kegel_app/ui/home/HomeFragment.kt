package org.tumba.kegel_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import org.tumba.kegel_app.databinding.FragmentHomeBinding
import org.tumba.kegel_app.databinding.LayoutProgressItemBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.hide
import org.tumba.kegel_app.utils.observeEvent
import org.tumba.kegel_app.utils.show
import javax.inject.Inject


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation(viewModel)
        viewModel.progressAnimation.observeEvent(viewLifecycleOwner) {
            animateProgress()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun animateProgress() {
        val dayViews = with(binding.progressItem.progress) {
            listOf(day1, day2, day3, day4, day5, day6, day7)
        }
        dayViews.forEach { it.root.hide() }
        binding.root.postDelayed(PROGRESS_ANIMATION_DELAY) {
            dayViews.mapIndexed { index, day ->
                animationProgressItem(index, day)
            }
        }
    }

    private fun animationProgressItem(index: Int, day: LayoutProgressItemBinding): Runnable {
        return binding.root.postDelayed(index * PROGRESS_ITEM_ANIMATION_DELAY) {
            TransitionManager.beginDelayedTransition(
                binding.progressItem.progress.root as ViewGroup,
                AutoTransition()
                    .setDuration(PROGRESS_ITEM_ANIMATION_DURATION)
                    .addTarget(day.root)
            )
            day.root.show()
        }
    }

    companion object {
        private const val PROGRESS_ANIMATION_DELAY = 100L
        private const val PROGRESS_ITEM_ANIMATION_DELAY = 100L
        private const val PROGRESS_ITEM_ANIMATION_DURATION = 300L
    }
}
