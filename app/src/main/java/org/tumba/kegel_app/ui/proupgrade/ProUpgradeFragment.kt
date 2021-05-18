package org.tumba.kegel_app.ui.proupgrade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.databinding.FragmentProUpgradeBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import org.tumba.kegel_app.utils.fragment.observeSnackbar
import org.tumba.kegel_app.utils.observeEvent
import javax.inject.Inject


class ProUpgradeFragment : Fragment() {

    private lateinit var binding: FragmentProUpgradeBinding

    @Inject
    lateinit var proUpgradeManager: ProUpgradeManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ProUpgradeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProUpgradeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        observeViewModel()
        observeNavigation(viewModel)
        viewModel.observeSnackbar(viewLifecycleOwner, requireContext(), binding.root)
        binding.close.setOnClickListener {
            viewModel.onClickClose()
            findNavController().navigateUp()
        }
    }

    private fun setupInsets() {
        view?.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.observeEvent(viewModel.startProPurchasingFlow) { startProPurchasingFlow ->
            if (startProPurchasingFlow) {
                proUpgradeManager.startProUpgradePurchaseFlow(requireActivity())
            }
        }
    }
}
