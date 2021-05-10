package org.tumba.kegel_app.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager
import org.tumba.kegel_app.R
import org.tumba.kegel_app.ui.exercise.ExerciseBackgroundMode
import org.tumba.kegel_app.ui.exercise.getExerciseBackgroundModeStringsMap

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

@BindingAdapter("bindNightModeValue")
fun bindNightModeValue(view: TextView, mode: Int) {
    val value = when (mode) {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.string.screen_settings_night_mode_system
        AppCompatDelegate.MODE_NIGHT_YES -> R.string.screen_settings_night_mode_on
        AppCompatDelegate.MODE_NIGHT_NO -> R.string.screen_settings_night_mode_off
        else -> R.string.screen_settings_night_mode_off
    }
    view.setText(value)
}

@BindingAdapter("bindBackgroundMode")
fun bindBackgroundMode(view: TextView, backgroundMode: ExerciseBackgroundMode?) {
    view.text = backgroundMode?.let { getExerciseBackgroundModeStringsMap()[it] }
        ?.let { view.resources.getString(it) }
        ?: String.Empty
}


@BindingAdapter("bindDrawableEndCompat")
fun bindDrawableEndCompat(view: TextView, drawable: Drawable?) {
    if (drawable != null) {
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            drawable,
            null
        )
    }
}