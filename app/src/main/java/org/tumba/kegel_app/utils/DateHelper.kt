package org.tumba.kegel_app.utils

import java.util.*
import javax.inject.Inject

class DateHelper @Inject constructor(){

    fun now(): Calendar = Calendar.getInstance()
}