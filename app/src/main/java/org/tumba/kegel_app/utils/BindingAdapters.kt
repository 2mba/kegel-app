package org.tumba.kegel_app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager

@BindingAdapter("bindGoneUnless", "animateVisibility", requireAll = false)
fun bindGoneUnless(view: View, visible: Boolean, animateVisibility: Boolean) {
    if (animateVisibility) {
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup)
    }
    if (visible) {
        view.show()
    } else {
        view.gone()
    }
}

@BindingAdapter("bindTextColorResources")
fun bindTextColorResources(view: TextView, @ColorRes color: Int) {
    view.setTextColor(ResourcesCompat.getColor(view.resources, color, null))
}

@BindingAdapter("bindTextColorStateResources")
fun bindTextColorStateResources(view: TextView, color: Int) {
    view.setTextColor(ResourcesCompat.getColorStateList(view.resources, color, null))
}
