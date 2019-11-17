package org.tumba.kegel_app.core.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment(
    private val viewCreationStrategy: IViewCreationStrategy = IViewCreationStrategy.NONE
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewCreationStrategy.createView(inflater, container, savedInstanceState)
    }
}

