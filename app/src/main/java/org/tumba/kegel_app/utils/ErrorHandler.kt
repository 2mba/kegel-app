package org.tumba.kegel_app.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

interface ErrorHandler {

    fun handleError(throwable: Throwable)
}

class ErrorHandlerImpl(private val context: Context) : ErrorHandler {

    override fun handleError(throwable: Throwable) {
        Log.e("ErrorHandlerImpl", "Handle error: ${throwable.message}", throwable)
    }
}

fun ErrorHandler.asCoroutineExceptionHandler(): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        this.handleError(throwable)
    }
}

object IgnoreErrorHandler : ErrorHandler {

    override fun handleError(throwable: Throwable) {
        Log.e("IgnoreErrorHandler", "Handle error: ${throwable.message}", throwable)
    }
}
