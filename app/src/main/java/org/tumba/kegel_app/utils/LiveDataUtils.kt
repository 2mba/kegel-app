package org.tumba.kegel_app.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LifecycleOwner.observe(lifeData: LiveData<T>, observer: (T) -> Unit) {
    lifeData.observe(this, Observer { observer(it) })
}

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun getContent(): T {
        return if (hasBeenHandled) {
            throw IllegalStateException("Event content already has been handled")
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

fun <T> LifecycleOwner.observeEvent(lifeData: LiveData<Event<T>>, observer: (T) -> Unit) {
    lifeData.observe(this) { event ->
        event.consume { observer(it) }
    }
}

fun <T: Any?> LiveData<Event<T>>.observeEvent(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner) { event ->
        event.consume { observer(it) }
    }
}

fun <T> Event<T>.consume(block: (T) -> Unit) {
    if (!hasBeenHandled) {
        block(getContent())
    }
}