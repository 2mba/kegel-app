package org.tumba.kegel_app.utils.fragment

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.tumba.kegel_app.ui.common.Navigable
import org.tumba.kegel_app.utils.observeEvent

fun Fragment.setToolbar(toolbar: Toolbar) {
    (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
}

val Fragment.actionBar: ActionBar?
    get() {
        return (activity as AppCompatActivity?)?.supportActionBar
    }

fun Fragment.observeNavigation(navigable: Navigable) {
    navigable.navigation.observeEvent(viewLifecycleOwner) { direction ->
        val controller = findNavController()
        if (controller.currentDestination?.getAction(direction.actionId) != null) {
            controller.navigate(direction)
        }
    }
}