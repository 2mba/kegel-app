package org.tumba.kegel_app.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tumba.kegel_app.R
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import javax.inject.Inject


class FirstExerciseChallengeDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FirstExerciseChallengeViewModel by viewModels { viewModelFactory }
    private val args by navArgs<FirstExerciseChallengeDialogFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        observeNavigation(viewModel)
        viewModel.exerciseType = args.type
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.screen_first_exercise_challenge_title)
            .setMessage(R.string.screen_first_exercise_challenge_message)
            .setNegativeButton(R.string.screen_first_exercise_challenge_no) { _, _ ->
                viewModel.onClickGoToExercise()
            }
            .setPositiveButton(R.string.screen_first_exercise_challenge_yes) { _, _ ->
                viewModel.onClickShowExerciseInfo()
            }
            .create()
    }
}