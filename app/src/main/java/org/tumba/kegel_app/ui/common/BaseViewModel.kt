package org.tumba.kegel_app.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import org.tumba.kegel_app.utils.Event

open class BaseViewModel(
    private val navigable: Navigable = NavigableImpl()
) : ViewModel(),
    ILifecycleOwner<ViewModelLifecycleEvent>,
    Navigable by navigable,
    SnackbarSource by SnackbarSourceImpl() {

    override val lifecycleObservable: LifecycleProxy<ViewModelLifecycleEvent> = LifecycleProxy()

    override fun onCleared() {
        super.onCleared()
        lifecycleObservable.onLifecycleEvent(ViewModelLifecycleEvent.OnDestroy)
    }
}

enum class ViewModelLifecycleEvent {
    OnDestroy
}

interface ILifecycleOwner<T> {

    val lifecycleObservable: ILifecycleObservable<T>
}

interface ILifecycleObservable<T> {

    fun observeLifecycle(listener: ILifecycleObserver<T>)
}

interface ILifecycleObserver<T> {

    fun onLifecycleEvent(event: T)
}

class LifecycleProxy<T> : ILifecycleObservable<T> {

    private val listeners = mutableSetOf<ILifecycleObserver<T>>()

    fun onLifecycleEvent(event: T) {
        listeners.forEach { it.onLifecycleEvent(event) }
    }

    override fun observeLifecycle(listener: ILifecycleObserver<T>) {
        listeners.add(listener)
    }
}

interface Navigable {

    val navigation: LiveData<Event<NavDirections>>

    fun navigate(direction: NavDirections)
}

class NavigableImpl : Navigable {

    override val navigation = MutableLiveData<Event<NavDirections>>()

    override fun navigate(direction: NavDirections) {
        navigation.value = Event(direction)
    }
}