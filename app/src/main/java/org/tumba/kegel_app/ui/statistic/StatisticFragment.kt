package org.tumba.kegel_app.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.chrisbanes.insetter.applyInsetter
import org.tumba.kegel_app.databinding.FragmentStatisticBinding
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.utils.ViewModelFactory
import org.tumba.kegel_app.utils.fragment.observeNavigation
import javax.inject.Inject
import kotlin.random.Random


class StatisticFragment : Fragment() {

    private lateinit var binding: FragmentStatisticBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: StatisticViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation(viewModel)
        setupInsets()
        val data = (0..23).mapIndexed { index, day ->
            val label = if (index == 0 || index == 23) "$day/03" else ""
            label to (Random.nextDouble(3.0).toFloat().takeIf { Random.nextInt(3) != 0 } ?: 0f)
        }

        binding.weeklyStatisticChartView.animate(data)
        // binding.weeklyStatisticChartView.labels = AxisLabels(20f)
        binding.weeklyStatisticChartView.labelsFormatter = { value ->
            "${value.toInt()} min"
        }

        val data2 = listOf(
            "Mon", "Tue", "Thu", "Fou", "Fiv", "Sat", "Sub"
        ).map { "$it" to (Random.nextDouble(130.0).toFloat().takeIf { Random.nextInt(3) != 0 } ?: 0f) }

        binding.weekDaysStatisticChartView.animate(data2)
        binding.weekDaysStatisticChartView.labelsFormatter = { value ->
            "${value.toInt()} min"
        }
    }


    private fun setupInsets() {
        view?.applyInsetter {
            type(statusBars = true) {
                padding()
            }
        }
    }
}
