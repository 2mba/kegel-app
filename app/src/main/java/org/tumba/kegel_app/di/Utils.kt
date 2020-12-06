package org.tumba.kegel_app.di

import android.app.Service
import androidx.fragment.app.Fragment
import org.tumba.kegel_app.KegelApplication
import org.tumba.kegel_app.di.component.AppComponent

val Fragment.appComponent: AppComponent
    get() = (requireActivity().application as KegelApplication).appComponent

val Service.appComponent: AppComponent
    get() = (application as KegelApplication).appComponent
