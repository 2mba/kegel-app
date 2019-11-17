package org.tumba.kegel_app.exercise.presentation.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
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
    }

    private fun observeViewModel() {
        viewModel.logs.observe(this, Observer { log -> this.log.text = log })
    }
}