package org.tumba.vacuum_app.ui.home

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentFirstEntryBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import javax.inject.Inject


class FirstEntryDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FirstEntryViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentFirstEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        observeNavigation(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        binding = FragmentFirstEntryBinding.inflate(layoutInflater, null, false)
        binding.btnAgree.setOnClickListener {
            viewModel.onClickSaveAgreement()
            dismissAllowingStateLoss()
        }
        binding.privacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
        binding.termsOfUsageLink.movementMethod = LinkMovementMethod.getInstance()
        return MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.screen_first_entry_title)
            .setView(binding.root)
            .create()
    }
}