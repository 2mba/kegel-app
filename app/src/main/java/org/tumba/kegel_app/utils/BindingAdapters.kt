package org.tumba.kegel_app.utils

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager

@BindingAdapter("bindGoneUnless", "animateVisibility", requireAll = false)
fun bindGoneUnless(view: View, visible: Boolean, animateVisibility: Boolean) {
    if (visible) {
        view.show()
    } else {
        view.gone()
    }
    if (animateVisibility) {
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup)
    }
}