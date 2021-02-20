package org.tumba.kegel_app.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.tumba.kegel_app.utils.Event

data class SnackbarData(
    val text: String
)

interface SnackbarSource {

    val snackbar: LiveData<Event<SnackbarData>>

    fun showSnackbar(snackbar: SnackbarData)
}

class SnackbarSourceImpl : SnackbarSource {

    override val snackbar = MutableLiveData<Event<SnackbarData>>()

    override fun showSnackbar(snackbar: SnackbarData) {
        this.snackbar.value = Event(snackbar)
    }
}