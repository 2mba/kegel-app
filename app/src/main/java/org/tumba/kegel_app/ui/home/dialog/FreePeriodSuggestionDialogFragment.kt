package org.tumba.kegel_app.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tumba.kegel_app.R
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import javax.inject.Inject


class FreePeriodSuggestionDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FreePeriodSuggestionViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        observeNavigation(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<View>(R.id.navFragment)?.let { view ->
            viewModel.observeSnackbar(
                this,
                requireContext(),
                view
            )
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.screen_free_period_suggestion_title)
            .setMessage(getString(R.string.screen_free_period_suggestion_message, viewModel.freePeriodDays))
            .setPositiveButton(R.string.screen_free_period_suggestion_yes) { _, _ -> viewModel.onClickYes() }
            .setNegativeButton(R.string.screen_free_period_suggestion_no) { _, _ -> viewModel.onClickCancel() }
            .create()
    }
}