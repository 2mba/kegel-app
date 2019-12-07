package org.tumba.kegel_app.exercise.presentation.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_exercise.*
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.presentation.fragment.BaseFragment
import org.tumba.kegel_app.core.presentation.fragment.ResourceCreationStrategy
import org.tumba.kegel_app.core.presentation.viewmodel.getViewModel
import org.tumba.kegel_app.di.Scope.SCOPE_APP
import org.tumba.kegel_app.di.Scope.SCOPE_EXERCISE
import org.tumba.kegel_app.exercise.di.getExerciseModule
import org.tumba.kegel_app.exercise.presentation.viewmodel.ExerciseViewModel
import toothpick.Toothpick
import javax.inject.Inject


class ExerciseFragment : BaseFragment(
    viewCreationStrategy = ResourceCreationStrategy(R.layout.fragment_exercise)
) {
    @Inject
    lateinit var viewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initScope()
        viewModel = getViewModel(ExerciseViewModel::class, SCOPE_EXERCISE)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeViewModel()
    }

    private fun initScope() {
        Toothpick.openScopes(SCOPE_APP, SCOPE_EXERCISE)
            .installModules(getExerciseModule())
    }

    private fun initUi() {
        progressBar.progressMax = 1F
        progressBar.progress = 1F
        btnPlay.visibility = View.INVISIBLE
        btnStop.visibility = View.INVISIBLE

        btnPlay.postDelayed({ btnPlay.show() }, 400)
        btnStop.postDelayed({ btnStop.show() }, 400)
    }

    private fun observeViewModel() {
        // viewModel.logs.observe(this, Observer { log -> this.log.text = log })
        var bool = true
        viewModel.exercise.observe(
            this,
            Observer { exercise ->
                if (this.exercise.text == exercise) return@Observer
                this.exercise.text = exercise
                val colorTo = if (bool) {
                    requireContext().getColor(R.color.colorAccent)
                } else {
                    requireContext().getColor(R.color.colorPrimary)
                }
                val colorFrom = if (bool) {
                    requireContext().getColor(R.color.colorPrimary)
                } else {
                    requireContext().getColor(R.color.colorAccent)
                }
                ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
                    addUpdateListener { value ->
                        progressBar.progressBarColor = value.animatedValue as Int
                        this@ExerciseFragment.exercise.setTextColor(value.animatedValue as Int)
                    }
                    start()
                }
                bool = !bool
            }
        )
        viewModel.repeatsRemain.observe(
            this,
            Observer { repeatsRemain -> repeats.text = "$repeatsRemain" }
        )
        viewModel.secondsRemain.observe(
            this,
            Observer { secondsRemain -> timer.text = "00:0$secondsRemain" }
        )
    }
}