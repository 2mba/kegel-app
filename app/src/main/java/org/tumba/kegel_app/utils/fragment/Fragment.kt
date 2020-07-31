package org.tumba.kegel_app.utils.fragment

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

fun Fragment.setToolbar(toolbar: Toolbar) {
    (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
}

val Fragment.actionBar: ActionBar?
    get() {
        return (activity as AppCompatActivity?)?.supportActionBar
    }
