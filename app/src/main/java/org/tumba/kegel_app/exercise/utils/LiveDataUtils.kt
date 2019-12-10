package org.tumba.kegel_app.exercise.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LifecycleOwner.observe(lifeData: LiveData<T>, observer: (T) -> Unit) {
    lifeData.observe(this, Observer { observer(it) })
}