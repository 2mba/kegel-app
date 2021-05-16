package org.tumba.kegel_app.ui.utils

import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer

class MenuVisibilityObserver(
    private val toolbar: Toolbar,
    @IdRes private val menuItemId: Int
) : Observer<Boolean> {

    override fun onChanged(value: Boolean) {
        toolbar.menu.findItem(menuItemId)?.isVisible = value
    }
}
