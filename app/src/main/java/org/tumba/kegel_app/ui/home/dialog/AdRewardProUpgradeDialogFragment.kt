package org.tumba.kegel_app.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.FragmentAdRewardProUpgradeBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import javax.inject.Inject


class AdRewardProUpgradeDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AdRewardProUpgradeViewModel by viewModels { viewModelFactory }
    private val args by navArgs<AdRewardProUpgradeDialogFragmentArgs>()

    private lateinit var binding: FragmentAdRewardProUpgradeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        observeNavigation(viewModel)
        viewModel.isCloseProUpgradeDialog = args.isCloseProUpgradeScreen
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeSnackbar(
            viewLifecycleOwner,
            requireContext(),
            activity?.findViewById(R.id.navFragment) ?: requireView()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        binding = FragmentAdRewardProUpgradeBinding.inflate(layoutInflater, null, false)

        return MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.screen_ad_reward_pro_upgrade_title)
            .setMessage(getString(R.string.screen_ad_reward_pro_upgrade_message, viewModel.freePeriodDays))
            .setView(binding.root)
            .create()
    }
}