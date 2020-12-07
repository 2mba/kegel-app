package org.tumba.kegel_app.analytics

import androidx.navigation.NavController
import javax.inject.Inject

class ScreenTracker @Inject constructor(private val analytics: Analytics) {

    fun init(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            analytics.trackScreen(destination.label.toString(), arguments)
        }
    }
}