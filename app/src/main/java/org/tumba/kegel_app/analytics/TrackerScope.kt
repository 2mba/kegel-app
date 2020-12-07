package org.tumba.kegel_app.analytics

import android.os.Bundle

interface TrackerScope

fun TrackerScope.click(what: String): String = "click_$what"

class ScreenTrackerHelper(private val analytics: Analytics, private val screen: String) {

    fun track(event: String, parameters: Bundle? = null) {
        analytics.track(event, screen, parameters)
    }
}
