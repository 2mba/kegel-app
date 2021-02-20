package org.tumba.kegel_app.utils.fragment

import android.content.Context
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.tumba.kegel_app.ui.common.Navigable
import org.tumba.kegel_app.ui.common.SnackbarSource
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

fun SnackbarSource.observeSnackbar(
    lifecycleOwner: LifecycleOwner,
    context: Context,
    view: View,
    snackbarModifier: (Snackbar.() -> Unit)? = null
) {
    snackbar.observeEvent(lifecycleOwner) { snackbar ->
        Snackbar.make(context, view, snackbar.text, Snackbar.LENGTH_SHORT)
            .also { snackbarModifier?.invoke(it) }
            .show()
    }
}