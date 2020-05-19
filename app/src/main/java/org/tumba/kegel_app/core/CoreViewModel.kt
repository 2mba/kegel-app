package org.tumba.kegel_app.core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class CoreViewModel : ViewModel(),
    ILifecycleOwner<ViewModelLifecycleEvent> {

    override val lifecycleObservable: LifecycleProxy<ViewModelLifecycleEvent> =
        LifecycleProxy()

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

fun Disposable.disposeOnDestroy(lifecycleOwner: ILifecycleOwner<ViewModelLifecycleEvent>): Disposable {
    return disposeBy(
        LifecycleDisposer.of(
            lifecycleOwner,
            ViewModelLifecycleEvent.OnDestroy
        )
    )
}

fun Disposable.disposeBy(disposer: IDisposer): Disposable {
    disposer.addDisposable(this)
    return this
}

interface IDisposer {

    fun addDisposable(disposable: Disposable)

    fun dispose()
}

class Disposer private constructor() : IDisposer {

    private val disposables = CompositeDisposable()

    override fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun dispose() {
        disposables.dispose()
    }

    companion object {

        fun create(): Disposer =
            Disposer()
    }
}

class LifecycleDisposer<T> private constructor(
    lifecycleOwner: ILifecycleOwner<T>,
    private val lifecycleEvent: T
) : IDisposer by Disposer.create() {

    init {
        lifecycleOwner.lifecycleObservable.observeLifecycle(object :
            ILifecycleObserver<T> {
            override fun onLifecycleEvent(event: T) {
                if (lifecycleEvent == event) {
                    dispose()
                }
            }
        })
    }

    companion object {

        fun <T> of(lifecycleOwner: ILifecycleOwner<T>, lifecycleEvent: T): LifecycleDisposer<T> {
            return LifecycleDisposer(
                lifecycleOwner,
                lifecycleEvent
            )
        }
    }
}
