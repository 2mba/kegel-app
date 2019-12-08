package org.tumba.kegel_app.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.home_item_exercise.*
import kotlinx.android.synthetic.main.home_item_hint.*
import org.tumba.kegel_app.R


class HomeFragment : Fragment() {

    private val rootNavController: NavController? by lazy {
        (parentFragment as NavHost?)?.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController()
        btnStartExercise.setOnClickListener { navigateToExercise() }
        itemHint.setOnClickListener { }
    }

    private fun navigateToExercise() {
        rootNavController?.navigate(
            R.id.action_screen_home_to_screen_exercise,
            null,
            null,
            FragmentNavigatorExtras(
                itemExercise to "itemExercise",
                itemExerciseTitle to "itemTitle"
            )
        )
    }
}
