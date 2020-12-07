package org.tumba.kegel_app.analytics

import javax.inject.Inject

class ExerciseNotificationTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "ExerciseNotification")

    fun trackCreated() {
        tracker.track("service_created")
    }

    fun trackPause() {
        tracker.track(click("pause"))
    }

    fun trackPlay() {
        tracker.track(click("play"))
    }

    fun trackStop() {
        tracker.track(click("stop"))
    }
}