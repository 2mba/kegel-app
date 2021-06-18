package org.tumba.kegel_app.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProviderViewModelFactory<T : ViewModel>(
    private val viewModelProvider: () -> T
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModelProvider.invoke() as T
    }
}
