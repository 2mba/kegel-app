package org.tumba.kegel_app.analytics

import androidx.core.os.bundleOf
import javax.inject.Inject

class ExerciseTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "Exercise")

    fun trackPause() {
        tracker.track(click("pause"))
    }

    fun trackPlay() {
        tracker.track(click("play"))
    }

    fun trackStop() {
        tracker.track(click("stop"))
    }

    fun trackChangeVibration(enabled: Boolean) {
        tracker.track("change_vibration", bundleOf("enabled" to enabled))
    }

    fun trackChangeNotification(enabled: Boolean) {
        tracker.track("change_notification", bundleOf("enabled" to enabled))
    }

    fun exitConfirmed() {
        tracker.track("exit_confirmed")
    }
}
