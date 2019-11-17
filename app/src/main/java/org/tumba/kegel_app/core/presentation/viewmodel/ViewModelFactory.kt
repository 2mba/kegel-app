package org.tumba.kegel_app.core.presentation.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import toothpick.Toothpick
import javax.inject.Inject
import kotlin.reflect.KClass

class ViewModelFactory @Inject constructor(
    private val scope: Any
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return Toothpick.openScope(scope).getInstance(modelClass) as T
    }
}

fun <T : ViewModel> Fragment.getViewModel(viewModelClass: KClass<T>, scope: Any): T {
    return ViewModelProviders.of(this, ViewModelFactory(scope)).get(viewModelClass.java)
}

